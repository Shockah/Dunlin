package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pl.shockah.dunlin.commands.result.CommandResult;

import javax.annotation.Nonnull;

public class CommandAdapter implements CommandListener {
    @Override
    public void onCommandReceived(@Nonnull CommandContext context, @Nonnull CommandPattern<?> pattern, @Nonnull Command<?, ?> command, @Nonnull String textInput) {
    }

    @Override
    public void onCommandExecuted(@Nonnull CommandContext context, @Nonnull CommandPattern<?> pattern, @Nonnull Command<?, ?> command, @Nonnull String textInput, @Nonnull CommandResult<?, ?> result) {
    }

    @Override
    public void onNonCommandMessageReceived(@Nonnull MessageReceivedEvent event) {
    }
}