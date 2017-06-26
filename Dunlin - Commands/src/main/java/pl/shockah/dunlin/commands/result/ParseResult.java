package pl.shockah.dunlin.commands.result;

import pl.shockah.dunlin.commands.Command;

public abstract class ParseResult<T> {
	public final Command<T, ?> command;

	protected ParseResult(Command<T, ?> command) {
		this.command = command;
	}
}