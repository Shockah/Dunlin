package pl.shockah.dunlin.commands;

import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;
import pl.shockah.dunlin.commands.result.ValueCommandResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChainCommand extends Command<Object, Object> {
	@Nonnull public final Command<Object, Object>[] commands;
	
	@SuppressWarnings("unchecked")
	public ChainCommand(@Nonnull Command<?, ?>[] commands) {
		this.commands = (Command<Object, Object>[])commands;
	}
	
	@Override
	@Nonnull public ParseResult<Object> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
		return commands[0].parseInput(context, textInput);
	}

	@Override
	@Nonnull public CommandResult<Object, Object> execute(@Nonnull CommandContext context, @Nullable Object input) {
		CommandResult<Object, Object> lastResult = commands[0].execute(context, input);
		for (int i = 1; i < commands.length; i++) {
			if (lastResult instanceof ErrorCommandResult<?, ?>)
				return lastResult;
			else
				lastResult = commands[i].execute(context, ((ValueCommandResult<?, ?>)lastResult).value);
		}
		return lastResult;
	}
}