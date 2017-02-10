package pl.shockah.dunlin.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import pl.shockah.util.ReadWriteMap;

public class DefaultNamedCommandProvider extends NamedCommandProvider<Object, Object> {
	protected final ReadWriteMap<String, NamedCommand<Object, Object>> commands = new ReadWriteMap<>(new HashMap<>());
	
	@Override
	public NamedCommand<Object, Object> provide(String name) {
		return commands.get(name.toLowerCase());
	}
	
	public void registerNamedCommand(NamedCommand<Object, Object> command) {
		//TODO: handle collisions
		commands.putAll(Arrays.stream(command.names).collect(Collectors.toMap(name -> name, name -> command)));
	}
	
	public void unregisterNamedCommand(NamedCommand<Object, Object> command) {
		commands.writeOperation(commands -> {
			commands.keySet().removeAll(Arrays.asList(command.names));
		});
	}
}