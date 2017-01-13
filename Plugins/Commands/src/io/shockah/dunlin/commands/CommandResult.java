package io.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;

public abstract class CommandResult<T> {
	CommandResult() {
	}
	
	public abstract Message getMessage();
}