package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pl.shockah.dunlin.commands.result.CommandResult;

public interface CommandListener {
    void onCommandReceived(CommandContext context, CommandPattern<?> pattern, Command<?, ?> command, String textInput);
    void onCommandExecuted(CommandContext context, CommandPattern<?> pattern, Command<?, ?> command, String textInput, CommandResult<?, ?> result);
    void onNonCommandMessageReceived(MessageReceivedEvent event);
}