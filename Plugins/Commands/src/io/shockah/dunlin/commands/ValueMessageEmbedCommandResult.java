package io.shockah.dunlin.commands;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class ValueMessageEmbedCommandResult<T> extends ValueCommandResult<T> {
	public final MessageEmbed embed;
	
	public ValueMessageEmbedCommandResult(T value) {
		this(value, null);
	}
	
	public ValueMessageEmbedCommandResult(T value, MessageEmbed embed) {
		super(value, null);
		this.embed = embed;
	}
	
	@Override
	public Message getMessage() {
		if (embed == null)
			return super.getMessage();
		
		MessageBuilder builder = new MessageBuilder();
		builder.setEmbed(embed);
		return builder.build();
	}
}