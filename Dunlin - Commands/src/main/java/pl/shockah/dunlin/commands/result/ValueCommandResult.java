package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

import java.awt.*;

public final class ValueCommandResult<T> extends CommandResult<T> {
	public static final Color EMBED_COLOR = new Color(0.5f, 0.9f, 0.5f);

	public final T value;

	public ValueCommandResult(Command<?, T> command, T value) {
		super(command);
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Message getMessage(Message originalMessage, Object input) {
		return ((Command<Object, T>)command).formatOutput(originalMessage, input, value);
	}
}