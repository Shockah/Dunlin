package pl.shockah.dunlin.ircbridge;

import java.util.regex.Pattern;

public final class RelayBotInfo {
	public final String nick;
	public final Pattern pattern;

	public RelayBotInfo(String nick, Pattern pattern) {
		this.nick = nick;
		this.pattern = pattern;
	}
}