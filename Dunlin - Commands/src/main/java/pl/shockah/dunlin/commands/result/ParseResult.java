package pl.shockah.dunlin.commands.result;

import pl.shockah.dunlin.commands.Command;

import javax.annotation.Nonnull;

public abstract class ParseResult<T> {
	@Nonnull public final Command<T, ?> command;

	protected ParseResult(@Nonnull Command<T, ?> command) {
		this.command = command;
	}
}