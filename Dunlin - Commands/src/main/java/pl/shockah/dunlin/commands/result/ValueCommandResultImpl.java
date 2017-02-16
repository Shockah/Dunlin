package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

public class ValueCommandResultImpl<Output> extends CommandResultImpl<Output> implements ValueCommandResult<Output> {
	protected final Output value;
	
	public ValueCommandResultImpl(Command<?, Output> command, Output value) {
		super(command);
		this.value = value;
	}

	@Override
	public Message getMessage() {
		return command.formatOutput(value);
	}

	@Override
	public Output get() {
		return value;
	}
}