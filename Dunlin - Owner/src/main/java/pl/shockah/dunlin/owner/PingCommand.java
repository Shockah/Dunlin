package pl.shockah.dunlin.owner;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;
import pl.shockah.dunlin.commands.result.ValueCommandResult;
import pl.shockah.dunlin.commands.result.ValueParseResult;

import java.time.temporal.ChronoUnit;

public class PingCommand extends NamedCommand<Void, Long> {
	public PingCommand() {
		super("ping");
	}

	@Override
	public ParseResult<Void> parseInput(Message message, String textInput) {
		return new ValueParseResult<>(this, null);
	}

	@Override
	public CommandResult<Long> execute(Message message, Void aVoid) {
		Message testMessage = message.getChannel().sendMessage("Pinging...").complete();
		long ping = message.getCreationTime().until(testMessage.getCreationTime(), ChronoUnit.MILLIS);
		testMessage.delete().queue();
		return new ValueCommandResult<>(this, ping);
	}

	@Override
	public Message formatOutput(Long ms) {
		return new MessageBuilder().append(String.format("\uD83D\uDD52 Pong: %dms", ms)).build();
	}
}