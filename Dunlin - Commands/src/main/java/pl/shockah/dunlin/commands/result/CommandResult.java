package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;
import pl.shockah.dunlin.commands.CommandContext;

public abstract class CommandResult<Input, Output> {
	public final Command<Input, Output> command;

	protected CommandResult(Command<Input, Output> command) {
		this.command = command;
	}

	public abstract Message getMessage(CommandContext context, Input input);
}