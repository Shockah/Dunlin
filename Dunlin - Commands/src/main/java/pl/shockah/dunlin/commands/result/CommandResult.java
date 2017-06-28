package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;
import pl.shockah.dunlin.commands.CommandContext;

public abstract class CommandResult<T> {
	public final Command<?, T> command;

	protected CommandResult(Command<?, T> command) {
		this.command = command;
	}

	public abstract Message getMessage(CommandContext context, T input);
}