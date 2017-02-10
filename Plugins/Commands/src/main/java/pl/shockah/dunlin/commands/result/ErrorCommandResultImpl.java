package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

public class ErrorCommandResultImpl<Output> extends CommandResultImpl<Output> implements ErrorCommandResult<Output> {
	protected final Message message;
	
	public ErrorCommandResultImpl(Command<?, Output> command, Message message) {
		super(command);
		this.message = message;
	}

	@Override
	public Message getMessage() {
		return message;
	}
}