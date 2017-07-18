package pl.shockah.dunlin.ircbridge;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import pl.shockah.dunlin.commands.*;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRCBridgePlugin extends ListenerPlugin implements CommandListener {
	@Dependency
	public CommandsPlugin commandsPlugin;

	public List<IRCBot> ircBots = new ArrayList<>();

	private net.dv8tion.jda.core.entities.User singleUser;
	private String avatarUrlFormat;
	private String normalAvatarReplacement;
	private String voicedAvatarReplacement;
	private String opAvatarReplacement;
	private int avatarVariations;

	public IRCBridgePlugin(PluginManager manager, Info info) {
		super(manager, info);
	}

	@Override
	protected void onLoad() {
		commandsPlugin.registerListener(this);

		if (manager.app.getShardManager().shards.length == 0)
			setupOnLoad();
	}

	@Override
	protected void onReady(ReadyEvent event) {
		setupOnLoad();
	}

	private void setupOnLoad() {
		try {
			JSONObject config = new JSONParser().parseObject(new String(Files.readAllBytes(Paths.get("ircbridge.json")), Charset.forName("UTF-8")));
			JSONObject global = config.getObjectOrEmpty("global");
			Guild guild = manager.app.getShardManager().getGuildById(global.getLong("guildId"));

			singleUser = null;
			global.onLong("singleUserId", singleUserId -> {
				singleUser = manager.app.getShardManager().getUserById(singleUserId);
			});

			JSONObject avatars = global.getObject("avatars");
			avatarUrlFormat = avatars.getString("urlFormat");
			normalAvatarReplacement = avatars.getString("normalReplacemement", "normal");
			voicedAvatarReplacement = avatars.getString("voicedReplacemement", "voiced");
			opAvatarReplacement = avatars.getString("opReplacemement", "op");
			avatarVariations = avatars.getInt("variations");

			for (JSONObject jServer : config.getListOrEmpty("servers").ofObjects()) {
				Configuration.Builder builder = new Configuration.Builder();

				builder.setEncoding(Charset.forName("UTF-8"));
				builder.setAutoReconnect(true);
				builder.setName(jServer.getString("nick", global.getOptionalString("nick")));
				builder.setAutoNickChange(true);

				String realName = jServer.getString("realName", global.getOptionalString("realName"));
				if (realName != null)
					builder.setRealName(realName);

				jServer.onObject("nickserv", jNickServ -> {
					jNickServ.onString("nick", builder::setNickservNick);
					builder.setNickservPassword(jNickServ.getString("password"));
				});

				String host = jServer.getString("host");
				Integer port = jServer.getOptionalInt("port");
				if (port == null)
					builder.addServer(host);
				else
					builder.addServer(host, port);

				Map<String, TextChannel> channelMap = new HashMap<>();
				for (JSONObject jChannel : jServer.getListOrEmpty("channels").ofObjects()) {
					String ircChannelName = jChannel.getString("name");

					TextChannel channel;
					Long channelId = jServer.getOptionalLong("discordChannelId");
					if (channelId != null) {
						channel = guild.getTextChannelById(channelId);
					} else {
						String channelName = jServer.getString("discordChannelName", ircChannelName.substring(1));
						List<TextChannel> channels = guild.getTextChannelsByName(channelName, true);
						if (!channels.isEmpty())
							channel = channels.get(0);
						else
							channel = (TextChannel)guild.getController().createTextChannel(channelName).complete();
					}

					builder.addAutoJoinChannel(ircChannelName);
					channelMap.put(ircChannelName, channel);
				}

				Configuration configuration = builder.buildConfiguration();
				IRCBot bot = new IRCBot(this, configuration, channelMap);
				bot.getConfiguration().getListenerManager().addListener(bot.listener);
				new Thread(() -> {
					try {
						bot.startBot();
					} catch (IOException | IrcException e) {
						e.printStackTrace();
					}
				}).start();
				ircBots.add(bot);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onUnload() {
		for (IRCBot bot : ircBots) {
			bot.close();
		}
		ircBots.clear();

		commandsPlugin.unregisterListener(this);
	}

	public String getAvatarUrl(User user, Channel channel) {
		int variation = (user.getNick().toLowerCase().hashCode() % avatarVariations) + 1;
		String url = avatarUrlFormat;
		url = url.replace("{$replacement}", getReplacement(user, channel));
		url = url.replace("{$variation}", String.valueOf(variation));
		return url;
	}

	private String getReplacement(User user, Channel channel) {
		if (channel.isOp(user))
			return opAvatarReplacement;
		else if (channel.hasVoice(user))
			return voicedAvatarReplacement;
		else
			return normalAvatarReplacement;
	}

	@Override
	public void onCommandReceived(CommandContext context, CommandPattern<?> pattern, Command<?, ?> command, String textInput) {
	}

	@Override
	public void onCommandExecuted(CommandContext context, CommandPattern<?> pattern, Command<?, ?> command, String textInput, CommandResult<?, ?> result) {
	}

	@Override
	public void onNonCommandMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor() == event.getJDA().getSelfUser())
			return;
		if (singleUser != null && event.getAuthor() != singleUser)
			return;

		for (IRCBot bot : ircBots) {
			String channelName = bot.reverseChannelMap.get(event.getTextChannel());
			if (channelName != null) {
				Channel channel = bot.getUserChannelDao().getChannel(channelName);
				if (channel != null)
					sendMessageToIrcChannel(event.getMessage(), channel);
				break;
			}
		}
	}

	private void sendMessageToIrcChannel(Message message, Channel channel) {
		String text = message.getContent();
		String[] split = text.split("\\r?\\n|\\r");
		for (String line : split) {
			if (singleUser == null)
				channel.send().message(String.format("<%s> %s", message.getGuild().getMember(message.getAuthor()).getEffectiveName(), line));
			else
				channel.send().message(line);
		}
	}
}