package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

import java.awt.*;

public class ValueCommandResultImpl<Output> extends CommandResultImpl<Output> implements ValueCommandResult<Output> {
	public static final Color EMBED_COLOR = new Color(0.5f, 0.9f, 0.5f);

	protected final Output value;
	
	public ValueCommandResultImpl(Command<?, Output> command, Output value) {
		super(command);
		this.value = value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Message getMessage(Message message, Object input) {
		return ((Command<Object, Output>)command).formatOutput(message, input, value);
	}

	@Override
	public Output get() {
		return value;
	}
}