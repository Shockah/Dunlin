package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;

public class DefaultCommandPattern extends CommandPattern {
	public static final String PARAMETERIZED_PATTERN = "^[%s](.*?)\\s(.*)$";
	
	@Override
	public boolean matches(Message message) {
		return false;
	}
}