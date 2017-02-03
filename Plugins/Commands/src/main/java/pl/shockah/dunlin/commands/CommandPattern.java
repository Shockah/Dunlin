package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;

public abstract class CommandPattern<T extends Command<?, ?>> {
	public abstract boolean matches(Message message);
	
	public abstract T getCommand(Message message);
}