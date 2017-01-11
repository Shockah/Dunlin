package io.shockah.dunlin.commands;

import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public abstract class CommandProvider {
	public abstract NamedCommand<?, ?> provide(GenericMessageEvent e, String commandName);
}