package pl.shockah.dunlin.ircbridge;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class IRCBot extends PircBotX {
	public static final Color PLUGIN_INFO_COLOR = new Color(0.7f, 0.7f, 1.0f);

	public final IRCBridgePlugin plugin;
	public final Listener listener;
	public final Map<String, TextChannel> channelMap;
	public final Map<TextChannel, String> reverseChannelMap;
	public final List<RelayBotInfo> relayBots;

	public IRCBot(IRCBridgePlugin plugin, Configuration configuration, Map<String, TextChannel> channelMap, List<RelayBotInfo> relayBots) {
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
			for (Channel ircChannel : event.getUserChannelDaoSnapshot().getAllChannels()) {
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
			TextChannel channel = channelMap.get(event.getChannel().getName());
			if (channel == null)
				return;

			String nick = event.getUser().getNick();
			String message = event.getMessage();
			String avatar = plugin.getAvatarUrl(event.getUser(), event.getChannel());

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
					.setAuthor(nick, null, avatar)
					.setDescription(message)
					.setTimestamp(Instant.now())
					.build()).queue();
		}

		@Override
		public void onAction(ActionEvent event) throws Exception {
			TextChannel channel = channelMap.get(event.getChannel().getName());
			if (channel == null)
				return;

			String nick = event.getUser().getNick();
			String message = event.getMessage();
			String avatar = plugin.getAvatarUrl(event.getUser(), event.getChannel());

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
					.setAuthor(nick, null, avatar)
					.setDescription(String.format("*%s*", message))
					.setTimestamp(Instant.now())
					.build()).queue();
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