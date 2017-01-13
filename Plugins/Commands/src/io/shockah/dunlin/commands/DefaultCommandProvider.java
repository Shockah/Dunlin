package io.shockah.dunlin.commands;

import java.util.HashMap;
import io.shockah.dunlin.util.ReadWriteMap;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public class DefaultCommandProvider extends CommandProvider {
	private final ReadWriteMap<String, NamedCommand<?, ?>> commands = new ReadWriteMap<>(new HashMap<>());
	
	@Override
	public NamedCommand<?, ?> provide(GenericMessageEvent e, String commandName) {
		return commands.get(commandName);
	}
	
	public void addNamedCommand(NamedCommand<?, ?> command) {
		commands.writeOperation(commands -> {
			for (String name : command.names) {
				commands.put(name, command);
			}
		});
	}
	
	public void removeNamedCommand(NamedCommand<?, ?> command) {
		commands.writeOperation(commands -> {
			for (String name : command.names) {
				commands.remove(name);
			}
		});
	}
}