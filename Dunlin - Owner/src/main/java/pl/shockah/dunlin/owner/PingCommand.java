package pl.shockah.dunlin.owner;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;
import pl.shockah.dunlin.commands.result.ValueCommandResult;
import pl.shockah.dunlin.commands.result.ValueParseResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.temporal.ChronoUnit;

public class PingCommand extends NamedCommand<Void, Long> {
	public PingCommand() {
		super("ping");
	}

	@Override
	@Nonnull public ParseResult<Void> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
		return new ValueParseResult<>(this, null);
	}

	@Override
	@Nonnull public CommandResult<Void, Long> execute(@Nonnull CommandContext context, @Nullable Void aVoid) {
		Message testMessage = context.message.getChannel().sendMessage("Pinging...").complete();
		long ping = context.message.getCreationTime().until(testMessage.getCreationTime(), ChronoUnit.MILLIS);
		testMessage.delete().queue();
		return new ValueCommandResult<>(this, ping);
	}

	@Override
	@Nullable public Message formatOutput(@Nullable Long ms) {
		return new MessageBuilder().append(String.format("\uD83D\uDD52 Pong: %dms", ms)).build();
	}
}