package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;
import pl.shockah.dunlin.commands.CommandContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public final class ValueCommandResult<Input, Output> extends CommandResult<Input, Output> {
	@Nonnull public static final Color EMBED_COLOR = new Color(0.5f, 0.9f, 0.5f);

	@Nullable public final Output value;

	public ValueCommandResult(@Nonnull Command<Input, Output> command, @Nullable Output value) {
		super(command);
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull public Message getMessage(@Nonnull CommandContext context, @Nullable Input input) {
		return command.formatOutput(context, input, value);
	}
}