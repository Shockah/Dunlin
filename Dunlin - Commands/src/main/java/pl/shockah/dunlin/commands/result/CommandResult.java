package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

public interface CommandResult<Output> {
	Command<?, Output> getCommand();
	
	Message getMessage();
}

/*public abstract class CommandResult<T> {
	CommandResult() {
	}
	
	public abstract Message getMessage();
	
	public static class Value<T> extends CommandResult<T> {
		public final Command<?, T> command;
		public final T value;
		public final Message message;
		
		public Value(Command<?, T> command, T value, Message message) {
			this.command = command;
			this.value = value;
			this.message = message;
		}
		
		@Override
		public Message getMessage() {
			return message;
		}
	}
}*/