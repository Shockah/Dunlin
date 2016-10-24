package io.shockah.dunlin.commands;

import net.dv8tion.jda.events.message.GenericMessageEvent;

public abstract class CommandPattern {
	public abstract PreparedCommandCall<?, ?> provide(GenericMessageEvent e) throws CommandParseException;
}