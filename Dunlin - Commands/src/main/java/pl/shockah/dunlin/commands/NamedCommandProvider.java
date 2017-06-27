package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;

public abstract class NamedCommandProvider<Input, Output> {
	public abstract NamedCommand<Input, Output> provide(Message message, String name);
}