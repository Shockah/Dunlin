package pl.shockah.dunlin.ircbridge;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import pl.shockah.dunlin.commands.*;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.jay.JSONObject;
import pl.shockah.jay.JSONParser;
import pl.shockah.pintail.PluginInfo;
import pl.shockah.util.ReadWriteList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class IRCBridgePlugin extends ListenerPlugin implements CommandListener {
	@Nonnull public final CommandsPlugin commandsPlugin;

	@Nonnull public final ReadWriteList<IRCBot> ircBots = new ReadWriteList<>(new ArrayList<>());

	public net.dv8tion.jda.core.entities.User singleUser;
	private Guild guild;
	private String avatarUrlFormat;
	private String normalAvatarReplacement;
	private String voicedAvatarReplacement;
	private String opAvatarReplacement;
	private int avatarVariations;
	private String awayMessage;

	@Nonnull private final OnlineCommand onlineCommand;

	public IRCBridgePlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info, @Nonnull @RequiredDependency CommandsPlugin commandsPlugin) {
		super(manager, info);
		this.commandsPlugin = commandsPlugin;

		commandsPlugin.registerListener(this);

		commandsPlugin.registerNamedCommand(
				onlineCommand = new OnlineCommand(this)
		);

		if (manager.app.getShardManager().shards.length == 0)
			setupOnLoad();
	}

	@Override
	protected void onUnload() {
		ircBots.iterate(IRCBot::close);
		ircBots.clear();

		commandsPlugin.unregisterNamedCommand(onlineCommand);

		commandsPlugin.unregisterListener(this);
	}

	@Override
	protected void onReady(ReadyEvent event) {
		setupOnLoad();
	}

	private void setupOnLoad() {
		try {
			JSONObject config = new JSONParser().parseObject(new String(Files.readAllBytes(Paths.get("ircbridge.json")), Charset.forName("UTF-8")));
			JSONObject global = config.getObjectOrEmpty("global");
			guild = manager.app.getShardManager().getGuildById(global.getLong("guildId"));

			singleUser = null;
			global.onLong("singleUserId", singleUserId -> {
				singleUser = manager.app.getShardManager().getUserById(singleUserId);
				awayMessage = global.getString("awayMessage", "Discord: {$status}");
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

				String nick = jServer.getOptionalString("nick");
				if (nick == null)
					nick = global.getOptionalString("nick");
				if (nick != null)
					builder.setName(nick);
				builder.setAutoNickChange(true);

				String realName = jServer.getOptionalString("realName");
				if (realName == null)
					realName = global.getOptionalString("realName");
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
					Long channelId = jChannel.getOptionalLong("discordChannelId");
					if (channelId != null) {
						channel = guild.getTextChannelById(channelId);
					} else {
						String channelName = jChannel.getString("discordChannelName", ircChannelName.substring(1));
						List<TextChannel> channels = guild.getTextChannelsByName(channelName, true);
						if (!channels.isEmpty())
							channel = channels.get(0);
						else
							channel = (TextChannel)guild.getController().createTextChannel(channelName).complete();
					}

					builder.addAutoJoinChannel(ircChannelName);
					channelMap.put(ircChannelName, channel);
				}

				List<RelayBotInfo> relayBots = new ArrayList<>();
				for (JSONObject jRelayBot : jServer.getListOrEmpty("relayBots").ofObjects()) {
					relayBots.add(new RelayBotInfo(jRelayBot.getString("name"), Pattern.compile(jRelayBot.getString("pattern"))));
				}

				Configuration configuration = builder.buildConfiguration();
				IRCBot bot = new IRCBot(this, configuration, channelMap, relayBots);
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

	public String getAvatarUrl(@Nonnull User user, @Nullable Channel channel) {
		return getAvatarUrl(user.getNick(), getReplacement(user, channel));
	}

	public String getAvatarUrl(@Nonnull String nick) {
		return getAvatarUrl(nick, "normal");
	}

	public String getAvatarUrl(@Nonnull String nick, @Nonnull String replacement) {
		int variation = (Math.abs(nick.hashCode()) % avatarVariations) + 1;
		String url = avatarUrlFormat;
		url = url.replace("{$replacement}", replacement);
		url = url.replace("{$variation}", String.valueOf(variation));
		return url;
	}

	private String getReplacement(@Nonnull User user, @Nullable Channel channel) {
		if (channel == null)
			return normalAvatarReplacement;
		if (channel.isOp(user))
			return opAvatarReplacement;
		else if (channel.hasVoice(user))
			return voicedAvatarReplacement;
		else
			return normalAvatarReplacement;
	}

	protected void setIrcAway(@Nonnull IRCBot bot) {
		if (singleUser == null)
			return;

		if (guild.getMember(singleUser).getOnlineStatus() == OnlineStatus.ONLINE) {
			bot.setIrcAway(null);
		} else {
			String message = awayMessage;
			message = message.replace("{$status}", guild.getMember(singleUser).getOnlineStatus().getKey());
			bot.setIrcAway(message);
		}
	}

	private void sendMessageToIrcChannel(@Nonnull Message message, @Nonnull Channel channel) {
		String content = message.getContent();
		if (!StringUtils.isEmpty(content)) {
			String[] split = content.split("\\r?\\n|\\r");
			for (String line : split) {
				if (singleUser == null)
					channel.send().message(String.format("<%s> %s", message.getGuild().getMember(message.getAuthor()).getEffectiveName(), line));
				else
					channel.send().message(line);
			}
		}

		for (Message.Attachment attachment : message.getAttachments()) {
			String line = String.format("[ Attachment: %s ]", attachment.getUrl());
			if (singleUser == null)
				channel.send().message(String.format("<%s> %s", message.getGuild().getMember(message.getAuthor()).getEffectiveName(), line));
			else
				channel.send().message(line);
		}
	}

	@Override
	protected void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
		if (singleUser == null)
			return;
		if (event.getUser() != singleUser)
			return;

		if (event.getPreviousOnlineStatus() != event.getGuild().getMember(event.getUser()).getOnlineStatus()) {
			ircBots.iterate(bot -> {
				if (bot.isConnected())
					setIrcAway(bot);
			});
		}
	}

	@Override
	public void onCommandReceived(@Nonnull CommandContext context, @Nonnull CommandPattern<?> pattern, @Nonnull Command<?, ?> command, @Nonnull String textInput) {
	}

	@Override
	public void onCommandExecuted(@Nonnull CommandContext context, @Nonnull CommandPattern<?> pattern, @Nonnull Command<?, ?> command, @Nonnull String textInput, @Nonnull CommandResult<?, ?> result) {
	}

	@Override
	public void onNonCommandMessageReceived(@Nonnull MessageReceivedEvent event) {
		if (event.getAuthor() == event.getJDA().getSelfUser())
			return;
		if (singleUser != null && event.getAuthor() != singleUser)
			return;

		Channel channel = getIrcChannel(event.getTextChannel());
		if (channel != null)
			sendMessageToIrcChannel(event.getMessage(), channel);
	}

	public Channel getIrcChannel(@Nonnull TextChannel discordChannel) {
		return ircBots.readOperation(ircBots -> {
			for (IRCBot bot : ircBots) {
				String channelName = bot.reverseChannelMap.get(discordChannel);
				if (channelName != null) {
					Channel channel = bot.getUserChannelDao().getChannel(channelName);
					if (channel != null)
						return channel;
					break;
				}
			}
			return null;
		});
	}
}