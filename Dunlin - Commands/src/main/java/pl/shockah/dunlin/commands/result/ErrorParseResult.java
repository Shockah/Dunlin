package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

public final class ErrorParseResult<T> extends ParseResult<T> {
	public final Message message;

	public ErrorParseResult(Command<T, ?> command, Message message) {
		super(command);
		this.message = message;
	}
}