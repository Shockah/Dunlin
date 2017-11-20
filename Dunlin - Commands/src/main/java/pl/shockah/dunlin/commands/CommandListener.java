package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pl.shockah.dunlin.commands.result.CommandResult;

import javax.annotation.Nonnull;

public interface CommandListener {
    void onCommandReceived(@Nonnull CommandContext context, @Nonnull CommandPattern<?> pattern, @Nonnull Command<?, ?> command, @Nonnull String textInput);
    void onCommandExecuted(@Nonnull CommandContext context, @Nonnull CommandPattern<?> pattern, @Nonnull Command<?, ?> command, @Nonnull String textInput, @Nonnull CommandResult<?, ?> result);
    void onNonCommandMessageReceived(@Nonnull MessageReceivedEvent event);
}