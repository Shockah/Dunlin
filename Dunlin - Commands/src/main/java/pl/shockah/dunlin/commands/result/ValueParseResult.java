package pl.shockah.dunlin.commands.result;

import pl.shockah.dunlin.commands.Command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public final class ValueParseResult<T> extends ParseResult<T> {
	@Nonnull public static final Color EMBED_COLOR = new Color(0.5f, 0.9f, 0.5f);

	@Nullable public final T value;

	public ValueParseResult(@Nonnull Command<T, ?> command, @Nullable T value) {
		super(command);
		this.value = value;
	}
}