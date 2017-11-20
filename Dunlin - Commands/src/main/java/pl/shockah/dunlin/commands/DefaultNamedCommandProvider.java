package pl.shockah.dunlin.commands;

import pl.shockah.util.ReadWriteMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class DefaultNamedCommandProvider extends NamedCommandProvider<Object, Object> {
	@Nonnull protected final ReadWriteMap<String, NamedCommand<Object, Object>> commands = new ReadWriteMap<>(new HashMap<>());
	
	@Override
	@Nullable public NamedCommand<Object, Object> provide(@Nonnull CommandContext context, @Nonnull String name) {
		return commands.get(name.toLowerCase());
	}
	
	public void registerNamedCommand(@Nonnull NamedCommand<Object, Object> command) {
		//TODO: handle collisions
		commands.putAll(Arrays.stream(command.names).collect(Collectors.toMap(name -> name, name -> command)));
	}
	
	public void unregisterNamedCommand(@Nonnull NamedCommand<Object, Object> command) {
		commands.writeOperation(commands -> {
			commands.keySet().removeAll(Arrays.asList(command.names));
		});
	}
}