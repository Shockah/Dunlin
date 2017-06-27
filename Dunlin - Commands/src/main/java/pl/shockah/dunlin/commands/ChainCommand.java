package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;
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
	public ParseResult<Object> parseInput(Message message, String textInput) {
		return commands[0].parseInput(message, textInput);
	}

	@Override
	public CommandResult<Object> execute(Message message, Object input) {
		CommandResult<Object> lastResult = commands[0].execute(message, input);
		for (int i = 1; i < commands.length; i++) {
			if (lastResult instanceof ErrorCommandResult<?>)
				return lastResult;
			else
				lastResult = commands[i].execute(message, ((ValueCommandResult<?>)lastResult).value);
		}
		return lastResult;
	}
}