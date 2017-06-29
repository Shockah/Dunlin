package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;

public abstract class Command<Input, Output> {
	public abstract ParseResult<Input> parseInput(CommandContext context, String textInput);
	
	public abstract CommandResult<Input, Output> execute(CommandContext context, Input input);

	public void output(CommandContext context, Input input, CommandResult<Input, Output> outputResult) {
		respond(context, outputResult.getMessage(context, input));
	}

	protected void respond(CommandContext context, Message response) {
		if (response != null)
			context.message.getChannel().sendMessage(response).queue();
	}
	
	public Message formatOutput(CommandContext context, Input input, Output output) {
		return formatOutput(output);
	}

	public Message formatOutput(Output output) {
		return new MessageBuilder().append(output.toString()).build();
	}
}