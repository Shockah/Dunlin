package pl.shockah.dunlin.commands.result;

import pl.shockah.dunlin.commands.Command;

import java.awt.*;

public final class ValueParseResult<T> extends ParseResult<T> {
	public static final Color EMBED_COLOR = new Color(0.5f, 0.9f, 0.5f);

	public final T value;

	public ValueParseResult(Command<T, ?> command, T value) {
		super(command);
		this.value = value;
	}
}