package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

public class ParseCommandResultImpl<Output> extends CommandResultImpl<Output> implements ParseCommandResult<Output> {
	protected final Output value;
	
	@SuppressWarnings("unchecked")
	public ParseCommandResultImpl(Command<?, ?> command, Output value) {
		super((Command<?, Output>)command);
		this.value = value;
	}

	@Override
	public Message getMessage() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Output get() {
		return value;
	}
}