package io.shockah.dunlin.commands;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

public abstract class MessageEmbedCommandResult<T> extends CommandResult<T> {
	@Override
	public Message getMessage() {
		MessageBuilder builder = new MessageBuilder();
		builder.setEmbed(getMessageEmbed());
		return builder.build();
	}
	
	public abstract MessageEmbed getMessageEmbed();
}