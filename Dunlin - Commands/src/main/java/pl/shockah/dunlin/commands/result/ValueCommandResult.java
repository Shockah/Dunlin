package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;
import pl.shockah.dunlin.commands.CommandContext;

import java.awt.*;

public final class ValueCommandResult<Input, Output> extends CommandResult<Input, Output> {
	public static final Color EMBED_COLOR = new Color(0.5f, 0.9f, 0.5f);

	public final Output value;

	public ValueCommandResult(Command<Input, Output> command, Output value) {
		super(command);
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Message getMessage(CommandContext context, Input input) {
		return command.formatOutput(context, input, value);
	}
}