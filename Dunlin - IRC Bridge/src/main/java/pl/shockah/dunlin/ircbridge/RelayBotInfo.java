package pl.shockah.dunlin.ircbridge;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public final class RelayBotInfo {
	@Nonnull public final String nick;
	@Nonnull public final Pattern pattern;

	public RelayBotInfo(@Nonnull String nick, @Nonnull Pattern pattern) {
		this.nick = nick;
		this.pattern = pattern;
	}
}