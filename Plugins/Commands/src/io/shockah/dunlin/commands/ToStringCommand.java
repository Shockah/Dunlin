package io.shockah.dunlin.commands;

import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public class ToStringCommand extends NamedCommand<Object, String> {
	public ToStringCommand() {
		super("tostring");
	}
	
	public String prepareChainedCallInput(GenericMessageEvent e, CommandResult<Object> previousResult) {
		return previousResult.toString();
	}

	@Override
	public Object parseInput(GenericMessageEvent e, String input) throws CommandParseException {
		return input;
	}

	@Override
	public CommandResult<String> call(CommandCall call, Object input) {
		return new ValueCommandResult<>(input.toString());
	}
}