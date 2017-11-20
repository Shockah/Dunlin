package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;

public final class CommandContext {
	@Nonnull public final Message message;
	@Nonnull public final User userContext;

	public CommandContext(@Nonnull Message message) {
		this(message, message.getAuthor());
	}

	public CommandContext(@Nonnull Message message, @Nonnull User userContext) {
		this.message = message;
		this.userContext = userContext;
	}
}