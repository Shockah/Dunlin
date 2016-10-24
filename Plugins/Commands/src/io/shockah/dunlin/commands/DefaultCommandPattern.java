package io.shockah.dunlin.commands;

import java.util.ArrayList;
import io.shockah.dunlin.util.ReadWriteList;
import net.dv8tion.jda.events.message.GenericMessageEvent;

public class DefaultCommandPattern extends CommandPattern {
	private final String[] prefixes;
	private final ReadWriteList<CommandProvider> providers = new ReadWriteList<>(new ArrayList<>());
	
	public DefaultCommandPattern(String[] prefixes) {
		this.prefixes = prefixes;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PreparedCommandCall<?, ?> provide(GenericMessageEvent e) throws CommandParseException {
		String message = e.getMessage().getContent();
		for (String prefix : prefixes) {
			if (message.startsWith(prefix) && message.length() > prefix.length()) {
				String[] spl = message.split("\\s");
				String commandList = spl[0].substring(prefix.length());
				String textInput = message.substring(Math.min(prefix.length() + commandList.length() + 1, message.length()));
				
				String[] commandNames = commandList.split(">");
				if (commandNames.length == 1) {
					Command<Object, Object> command = (Command<Object, Object>)providers.firstResult(provider -> provider.provide(e, commandNames[0]));
					if (command == null)
						return null;
					return new PreparedCommandCall<>(command, command.parseInput(e, textInput));
				} else {
					Command<?, ?>[] commands = new Command[commandNames.length];
					for (int i = 0; i < commandNames.length; i++) {
						String commandName = commandNames[i];
						Command<?, ?> command = providers.firstResult(provider -> provider.provide(e, commandName));
						if (command == null)
							return null;
						commands[i] = command;
					}
					Command<Object, Object> command = new ChainCommand<>(commands);
					return new PreparedCommandCall<>(command, command.parseInput(e, textInput));
				}
			}
		}
		return null;
	}
	
	public void addProvider(CommandProvider provider) {
		providers.add(provider);
	}
	
	public void removeProvider(CommandProvider provider) {
		providers.remove(provider);
	}
	
	public NamedCommand<?, ?> findCommand(GenericMessageEvent e, String name) {
		return providers.firstResult(provider -> provider.provide(e, name));
	}
}