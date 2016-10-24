package io.shockah.dunlin.commands;

import java.util.List;
import net.dv8tion.jda.events.message.GenericMessageEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;

public final class CommandCall {
	public final GenericMessageEvent event;
	public final Medium inputMedium;
	public Medium outputMedium = null;
	
	public CommandCall(GenericMessageEvent event) {
		this.event = event;
		
		if (event instanceof GuildMessageReceivedEvent)
			inputMedium = Medium.Channel;
		else if (event instanceof PrivateMessageReceivedEvent)
			inputMedium = Medium.Private;
		else
			throw new IllegalArgumentException();
	}
	
	public void respond(List<String> lines) {
		for (String line : lines) {
			respond(line);
		}
	}
	
	public void respond(String line) {
		Medium medium = outputMedium;
		if (medium == null)
			medium = inputMedium;
		
		switch (medium) {
			case Channel:
				((GuildMessageReceivedEvent)event).getChannel().sendMessage(line);
				break;
			case Private:
				event.getAuthor().getPrivateChannel().sendMessage(line);
				break;
		}
	}
	
	public static enum Medium {
		Channel, Private;
	}
}