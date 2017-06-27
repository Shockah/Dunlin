package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

public abstract class CommandResult<T> {
	public final Command<?, T> command;

	protected CommandResult(Command<?, T> command) {
		this.command = command;
	}

	public abstract Message getMessage(Message originalMessage, T input);
}