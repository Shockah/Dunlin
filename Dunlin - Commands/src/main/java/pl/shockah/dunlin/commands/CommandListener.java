package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pl.shockah.dunlin.commands.result.CommandResult;

public interface CommandListener {
    void onCommandReceived(MessageReceivedEvent event, CommandPattern<?> pattern, Command<?, ?> command, String textInput);
    void onCommandExecuted(MessageReceivedEvent event, CommandPattern<?> pattern, Command<?, ?> command, String textInput, CommandResult<Object> result);
    void onNonCommandMessageReceived(MessageReceivedEvent event);
}