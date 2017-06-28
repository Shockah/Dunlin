package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pl.shockah.dunlin.commands.result.CommandResult;

public class CommandAdapter implements CommandListener {
    @Override
    public void onCommandReceived(CommandContext context, CommandPattern<?> pattern, Command<?, ?> command, String textInput) {
    }

    @Override
    public void onCommandExecuted(CommandContext context, CommandPattern<?> pattern, Command<?, ?> command, String textInput, CommandResult<Object> result) {
    }

    @Override
    public void onNonCommandMessageReceived(MessageReceivedEvent event) {
    }
}