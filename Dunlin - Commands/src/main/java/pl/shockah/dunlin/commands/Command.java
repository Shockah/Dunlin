package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Command<Input, Output> {
	@Nonnull public abstract ParseResult<Input> parseInput(@Nonnull CommandContext context, @Nonnull String textInput);
	
	@Nonnull public abstract CommandResult<Input, Output> execute(@Nonnull CommandContext context, @Nullable Input input);

	public void output(@Nonnull CommandContext context, @Nullable Input input, @Nonnull CommandResult<Input, Output> outputResult) {
		respond(context, outputResult.getMessage(context, input));
	}

	protected void respond(@Nonnull CommandContext context, @Nullable Message response) {
		if (response != null)
			context.message.getChannel().sendMessage(response).queue();
	}
	
	public Message formatOutput(@Nonnull CommandContext context, @Nullable Input input, @Nullable Output output) {
		return formatOutput(output);
	}

	public Message formatOutput(@Nullable Output output) {
		return new MessageBuilder().append(String.valueOf(output)).build();
	}
}