package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;
import pl.shockah.dunlin.commands.CommandContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CommandResult<Input, Output> {
	@Nonnull public final Command<Input, Output> command;

	protected CommandResult(@Nonnull Command<Input, Output> command) {
		this.command = command;
	}

	@Nonnull public abstract Message getMessage(@Nonnull CommandContext context, @Nullable Input input);
}