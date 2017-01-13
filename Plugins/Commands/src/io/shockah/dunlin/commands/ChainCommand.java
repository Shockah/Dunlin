package io.shockah.dunlin.commands;

import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public class ChainCommand<T, R> extends Command<T, R> {
	private final Command<?, ?>[] commands;
	
	public ChainCommand(Command<?, ?>[] commands) {
		this.commands = commands;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T prepareChainedCallInput(GenericMessageEvent e, ValueCommandResult<T> previousResult) {
		Command<Object, Object> objectCommand = (Command<Object, Object>)commands[0];
		return (T)objectCommand.prepareChainedCallInput(e, (ValueCommandResult<Object>)previousResult);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T convertToInput(GenericMessageEvent e, Object input) throws CommandParseException {
		return (T)input;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T parseInput(GenericMessageEvent e, String input) throws CommandParseException {
		return (T)commands[0].parseInput(e, input);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<R> call(CommandCall call, T input) {
		try {
			Object value = input;
			CommandResult<?> previousResult = null;
			for (Command<?, ?> genericCommand : commands) {
				Command<Object, Object> objectCommand = (Command<Object, Object>)genericCommand;
				Object inputToCall = value;
				if (previousResult != null)
					inputToCall = objectCommand.prepareChainedCallInput(call.event, (ValueCommandResult<Object>)previousResult);
				try {
					inputToCall = objectCommand.convertToInput(call.event, inputToCall);
				} catch (Exception e) {
					return new ExceptionCommandResult<>(e);
				}
				previousResult = objectCommand.call(call, inputToCall);
				if (previousResult instanceof ErrorCommandResult<?>)
					return (ErrorCommandResult<R>)previousResult;
				else if (previousResult instanceof ValueCommandResult<?>)
					value = ((ValueCommandResult<?>)previousResult).value;
				else
					throw new ClassCastException(String.format("Unknown CommandResult subclass %s.", previousResult.getClass().getName()));
			}
			return new ValueCommandResult<>((R)value);
		} catch (ClassCastException e) {
			return new ExceptionCommandResult<>(e);
		}
	}
}