package pl.shockah.dunlin.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import pl.shockah.util.ReadWriteMap;

public class DefaultNamedCommandProvider<Input, Output> extends NamedCommandProvider<Input, Output> {
	protected final ReadWriteMap<String, NamedCommand<Input, Output>> commands = new ReadWriteMap<>(new HashMap<>());
	
	@Override
	public NamedCommand<Input, Output> provide(String name) {
		return commands.get(name.toLowerCase());
	}
	
	public void register(NamedCommand<Input, Output> command) {
		//TODO: handle collisions
		commands.putAll(Arrays.stream(command.names).collect(Collectors.toMap(name -> name, name -> command)));
	}
	
	public void unregister(NamedCommand<Input, Output> command) {
		commands.writeOperation(commands -> {
			commands.keySet().removeAll(Arrays.asList(command.names));
		});
	}
}