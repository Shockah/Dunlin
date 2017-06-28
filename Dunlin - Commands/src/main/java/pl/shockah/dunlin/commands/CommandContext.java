package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public final class CommandContext {
	public final Message message;
	public User userContext;

	public CommandContext(Message message) {
		this(message, message.getAuthor());
	}

	public CommandContext(Message message, User userContext) {
		this.message = message;
		this.userContext = userContext;
	}
}