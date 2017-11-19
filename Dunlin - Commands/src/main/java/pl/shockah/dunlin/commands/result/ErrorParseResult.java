package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

import javax.annotation.Nonnull;

public final class ErrorParseResult<T> extends ParseResult<T> {
	@Nonnull public final Message message;

	public ErrorParseResult(@Nonnull Command<T, ?> command, @Nonnull Message message) {
		super(command);
		this.message = message;
	}
}