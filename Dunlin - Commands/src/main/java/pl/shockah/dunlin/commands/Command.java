package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;

public abstract class Command<Input, Output> {
	public abstract ParseResult<Input> parseInput(Message message, String textInput);
	
	public abstract CommandResult<Output> execute(Message message, Input input);
	
	public Message formatOutput(Message message, Input input, Output output) {
		return formatOutput(output);
	}

	public Message formatOutput(Output output) {
		return new MessageBuilder().append(output.toString()).build();
	}
}