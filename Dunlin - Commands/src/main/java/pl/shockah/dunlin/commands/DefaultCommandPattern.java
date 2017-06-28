package pl.shockah.dunlin.commands;

import pl.shockah.dunlin.settings.MessageSettingScope;
import pl.shockah.util.ReadWriteSet;

import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultCommandPattern extends CommandPattern<NamedCommand<?, ?>> {
	public static final String PARAMETERIZED_PATTERN = "^[%s]([^\\s]*?)(?:\\s(.*))?$";
	
	final CommandsPlugin plugin;
	protected final ReadWriteSet<NamedCommandProvider<?, ?>> namedCommandProviders = new ReadWriteSet<>(new LinkedHashSet<>());
	
	public DefaultCommandPattern(CommandsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean matches(CommandContext context) {
		String content = context.message.getRawContent();
		for (String prefix : plugin.prefixesSetting.get(new MessageSettingScope(context.message)).split("\\s")) {
			if (Pattern.compile(String.format(PARAMETERIZED_PATTERN, prefix), Pattern.DOTALL | Pattern.MULTILINE).matcher(content).find())
				return true;
		}
		return false;
	}

	@Override
	public CommandPatternMatch<NamedCommand<?, ?>> getCommand(CommandContext context) {
		String commandName = null;
		String input = null;
		String content = context.message.getRawContent();
		for (String prefix : plugin.prefixesSetting.get(new MessageSettingScope(context.message)).split("\\s")) {
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
		NamedCommand<?, ?> command = namedCommandProviders.firstResult(provider -> (NamedCommand<?, ?>)provider.provide(context, f_commandName));
		return command != null ? new CommandPatternMatch<>(command, input) : null;
	}
	
	public void registerNamedCommandProvider(NamedCommandProvider<?, ?> namedCommandProvider) {
		namedCommandProviders.add(namedCommandProvider);
	}
	
	public void unregisterNamedCommandProvider(NamedCommandProvider<?, ?> namedCommandProvider) {
		namedCommandProviders.remove(namedCommandProvider);
	}
}