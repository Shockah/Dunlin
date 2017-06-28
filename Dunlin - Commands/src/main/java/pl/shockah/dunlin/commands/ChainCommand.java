package pl.shockah.dunlin.commands;

import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;
import pl.shockah.dunlin.commands.result.ValueCommandResult;

public class ChainCommand extends Command<Object, Object> {
	public final Command<Object, Object>[] commands;
	
	@SuppressWarnings("unchecked")
	public ChainCommand(Command<?, ?>[] commands) {
		this.commands = (Command<Object, Object>[])commands;
	}
	
	@Override
	public ParseResult<Object> parseInput(CommandContext context, String textInput) {
		return commands[0].parseInput(context, textInput);
	}

	@Override
	public CommandResult<Object> execute(CommandContext context, Object input) {
		CommandResult<Object> lastResult = commands[0].execute(context, input);
		for (int i = 1; i < commands.length; i++) {
			if (lastResult instanceof ErrorCommandResult<?>)
				return lastResult;
			else
				lastResult = commands[i].execute(context, ((ValueCommandResult<?>)lastResult).value);
		}
		return lastResult;
	}
}