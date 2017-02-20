package pl.shockah.dunlin.commands;

import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.util.ReadWriteSet;

public class DefaultCommandPattern extends CommandPattern<NamedCommand<?, ?>> {
	public static final String PARAMETERIZED_PATTERN = "^[%s]([^\\s]*?)(?:\\s(.*))?$";
	
	final CommandsPlugin plugin;
	protected final ReadWriteSet<NamedCommandProvider<?, ?>> namedCommandProviders = new ReadWriteSet<>(new LinkedHashSet<>());
	
	public DefaultCommandPattern(CommandsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean matches(Message message) {
		String content = message.getRawContent();
		for (String prefix : plugin.prefixesSetting.get(message).split("\\s")) {
			if (Pattern.compile(String.format(PARAMETERIZED_PATTERN, prefix), Pattern.DOTALL | Pattern.MULTILINE).matcher(content).find())
				return true;
		}
		return false;
	}

	@Override
	public CommandPatternMatch<NamedCommand<?, ?>> getCommand(Message message) {
		String commandName = null;
		String input = null;
		String content = message.getRawContent();
		for (String prefix : plugin.prefixesSetting.get(message).split("\\s")) {
			Matcher m = Pattern.compile(String.format(PARAMETERIZED_PATTERN, prefix), Pattern.DOTALL | Pattern.MULTILINE).matcher(content);
			if (m.find()) {
				commandName = m.group(1);
				input = m.groupCount() >= 2 ? m.group(2) : "";
				break;
			}
		}

		if (commandName == null)
			return null;
		
		final String f_commandName = commandName;
		NamedCommand<?, ?> command = namedCommandProviders.firstResult(provider -> (NamedCommand<?, ?>)provider.provide(f_commandName));
		return command != null ? new CommandPatternMatch<>(command, input) : null;
	}
	
	public void registerNamedCommandProvider(NamedCommandProvider<?, ?> namedCommandProvider) {
		namedCommandProviders.add(namedCommandProvider);
	}
	
	public void unregisterNamedCommandProvider(NamedCommandProvider<?, ?> namedCommandProvider) {
		namedCommandProviders.remove(namedCommandProvider);
	}
}