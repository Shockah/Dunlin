package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

public interface CommandResult<Output> {
	Command<?, Output> getCommand();
	
	Message getMessage(Message message, Object input);
}