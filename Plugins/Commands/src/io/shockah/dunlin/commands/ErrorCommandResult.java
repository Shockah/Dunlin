package io.shockah.dunlin.commands;

import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class ErrorCommandResult<T> extends MessageEmbedCommandResult<T> {
	public final Color EMBED_COLOR = new Color(1.0f, 0.0f, 0.0f);
	
	public final String title;
	public final String description;
	
	public ErrorCommandResult(String description) {
		this(null, description);
	}
	
	public ErrorCommandResult(String title, String description) {
		this.title = title;
		this.description = description;
	}

	@Override
	public MessageEmbed getMessageEmbed() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(EMBED_COLOR);
		
		if (title != null)
			builder.setTitle(title);
		if (description != null)
			builder.setDescription(description);
		
		return builder.build();
	}
}