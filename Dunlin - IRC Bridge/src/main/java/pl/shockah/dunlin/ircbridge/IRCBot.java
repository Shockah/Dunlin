package pl.shockah.dunlin.ircbridge;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.pircbotx.snapshot.UserChannelDaoSnapshot;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class IRCBot extends PircBotX {
	@Nonnull public static final Color PLUGIN_INFO_COLOR = new Color(0.7f, 0.7f, 1.0f);

	@Nonnull public final IRCBridgePlugin plugin;
	@Nonnull public final Listener listener;
	@Nonnull public final Map<String, TextChannel> channelMap;
	@Nonnull public final Map<TextChannel, String> reverseChannelMap;
	@Nonnull public final List<RelayBotInfo> relayBots;

	public IRCBot(@Nonnull IRCBridgePlugin plugin, @Nonnull Configuration configuration, @Nonnull Map<String, TextChannel> channelMap, @Nonnull List<RelayBotInfo> relayBots) {
		super(configuration);
		this.plugin = plugin;
		listener = new IRCBotListener();
		this.channelMap = Collections.unmodifiableMap(channelMap);
		this.relayBots = Collections.unmodifiableList(relayBots);

		Map<TextChannel, String> reverseChannelMap = new HashMap<>();
		for (Map.Entry<String, TextChannel> entry : channelMap.entrySet()) {
			reverseChannelMap.put(entry.getValue(), entry.getKey());
		}
		this.reverseChannelMap = Collections.unmodifiableMap(reverseChannelMap);
	}

	private class IRCBotListener extends ListenerAdapter {
		private void sendMessage(MessageChannel channel, String nick, String nickFormat, String message, String avatar) {
			for (RelayBotInfo relayBot : relayBots) {
				if (nick.equalsIgnoreCase(relayBot.nick)) {
					Matcher m = relayBot.pattern.matcher(message);
					if (m.find()) {
						nick = String.format("%s *via %s*", m.group("nick"), nick);
						message = m.group("message");
						avatar = plugin.getAvatarUrl(nick);
						break;
					}
				}
			}

			channel.sendMessage(new EmbedBuilder()
					.setAuthor(String.format(nickFormat, nick), null, avatar)
					.setDescription(message)
					.setTimestamp(Instant.now())
					.build()).queue();
		}

		@Override
		public void onConnect(ConnectEvent event) throws Exception {
			plugin.setIrcAway(IRCBot.this);

			for (Channel ircChannel : getUserChannelDao().getAllChannels()) {
				TextChannel channel = channelMap.get(ircChannel.getName());
				if (channel == null)
					return;

				channel.sendMessage(new EmbedBuilder()
						.setColor(PLUGIN_INFO_COLOR)
						.setDescription(String.format("```diff\n+ Connected to %s.\n```", getConfiguration().getServers().get(0).getHostname()))
						.setTimestamp(Instant.now())
						.build()).queue();
			}
		}

		@Override
		public void onDisconnect(DisconnectEvent event) throws Exception {
			UserChannelDaoSnapshot snapshot = event.getUserChannelDaoSnapshot();
			if (snapshot == null)
				return;

			for (Channel ircChannel : snapshot.getAllChannels()) {
				TextChannel channel = channelMap.get(ircChannel.getName());
				if (channel == null)
					return;

				channel.sendMessage(new EmbedBuilder()
						.setColor(PLUGIN_INFO_COLOR)
						.setDescription(String.format("```diff\n- Disconnected from %s.\n```", getConfiguration().getServers().get(0).getHostname()))
						.setTimestamp(Instant.now())
						.build()).queue();
			}
		}

		@Override
		public void onMessage(MessageEvent event) throws Exception {
			if (event.getUser() == null)
				return;

			TextChannel channel = channelMap.get(event.getChannel().getName());
			if (channel == null)
				return;

			String nick = event.getUser().getNick();
			String message = event.getMessage();
			String avatar = plugin.getAvatarUrl(event.getUser(), event.getChannel());

			sendMessage(channel, nick, "%s", message, avatar);
		}

		@Override
		public void onAction(ActionEvent event) throws Exception {
			if (event.getChannel() == null || event.getUser() == null)
				return;

			TextChannel channel = channelMap.get(event.getChannel().getName());
			if (channel == null)
				return;

			String nick = event.getUser().getNick();
			String message = event.getMessage();
			String avatar = plugin.getAvatarUrl(event.getUser(), event.getChannel());

			sendMessage(channel, nick, "*%s*", message, avatar);
		}

		@Override
		public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
			if (plugin.singleUser == null)
				return;
			if (event.getUser() == null)
				return;

			String nick = event.getUser().getNick();
			String message = event.getMessage();
			String avatar = plugin.getAvatarUrl(event.getUser(), null);

			plugin.singleUser.openPrivateChannel().queue(channel ->
					sendMessage(channel, nick, String.format("%%s @ %s", getConfiguration().getServers().get(0).getHostname()), message, avatar)
			);
		}

		@Override
		public void onNotice(NoticeEvent event) throws Exception {
			if (event.getUser() == null)
				return;

			if (event.getChannel() == null) {
				if (plugin.singleUser == null)
					return;

				String nick = event.getUser().getNick();
				String message = event.getMessage();
				String avatar = plugin.getAvatarUrl(event.getUser(), null);

				plugin.singleUser.openPrivateChannel().queue(channel ->
						sendMessage(channel, nick, String.format("-- %%s @ %s", getConfiguration().getServers().get(0).getHostname()), message, avatar)
				);
			} else {
				TextChannel channel = channelMap.get(event.getChannel().getName());
				if (channel == null)
					return;

				String nick = event.getUser().getNick();
				String message = event.getMessage();
				String avatar = plugin.getAvatarUrl(event.getUser(), event.getChannel());

				sendMessage(channel, nick, "-- %s", message, avatar);
			}
		}
	}

	public void setIrcAway(String awayMessage) {
		if (StringUtils.isEmpty(awayMessage)) {
			sendRaw().rawLine("AWAY");
		} else {
			String message = awayMessage;
			message = message.replace("{$status}", awayMessage);
			sendRaw().rawLine(String.format("AWAY :%s", message));
		}
	}
}