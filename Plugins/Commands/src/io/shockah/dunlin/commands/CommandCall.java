package io.shockah.dunlin.commands;

import java.util.List;
import io.shockah.dunlin.MessageMedium;
import net.dv8tion.jda.events.message.GenericMessageEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;

public final class CommandCall {
	public final GenericMessageEvent event;
	public final MessageMedium inputMedium;
	public MessageMedium outputMedium = null;
	
	public CommandCall(GenericMessageEvent event) {
		this.event = event;
		
		if (event instanceof GuildMessageReceivedEvent)
			inputMedium = new MessageMedium.Channel(((GuildMessageReceivedEvent)event).getChannel());
		else if (event instanceof PrivateMessageReceivedEvent)
			inputMedium = new MessageMedium.Private(event.getAuthor());
		else
			throw new IllegalArgumentException(String.format("Illegal event type: %s", event.getClass().getName()));
	}
	
	public void respond(List<String> lines) {
		for (String line : lines) {
			respond(line);
		}
	}
	
	public void respond(String line) {
		MessageMedium medium = outputMedium;
		if (medium == null)
			medium = inputMedium;
		
		medium.sendMessage(line);
	}
}