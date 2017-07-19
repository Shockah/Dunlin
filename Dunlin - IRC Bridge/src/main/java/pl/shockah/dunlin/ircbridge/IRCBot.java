package pl.shockah.dunlin.ircbridge;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IRCBot extends PircBotX {
	public final IRCBridgePlugin plugin;
	public final Listener listener;
	public final Map<String, TextChannel> channelMap;
	public final Map<TextChannel, String> reverseChannelMap;

	public IRCBot(IRCBridgePlugin plugin, Configuration configuration, Map<String, TextChannel> channelMap) {
		super(configuration);
		this.plugin = plugin;
		listener = new IRCBotListener();
		this.channelMap = Collections.unmodifiableMap(channelMap);

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
		}

		@Override
		public void onMessage(MessageEvent event) throws Exception {
			TextChannel channel = channelMap.get(event.getChannel().getName());
			if (channel == null)
				return;

			channel.sendMessage(new EmbedBuilder()
					.setAuthor(event.getUser().getNick(), null, plugin.getAvatarUrl(event.getUser(), event.getChannel()))
					.setDescription(event.getMessage())
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