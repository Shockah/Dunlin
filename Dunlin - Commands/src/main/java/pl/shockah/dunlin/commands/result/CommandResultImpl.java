package pl.shockah.dunlin.commands.result;

import pl.shockah.dunlin.commands.Command;

public abstract class CommandResultImpl<Output> implements CommandResult<Output> {
	protected final Command<?, Output> command;
	
	public CommandResultImpl(Command<?, Output> command) {
		this.command = command;
	}
	
	@Override
	public Command<?, Output> getCommand() {
		return command;
	}
}