package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;

public abstract class CommandPattern {
	public abstract boolean matches(Message message);
}