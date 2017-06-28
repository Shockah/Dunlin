package pl.shockah.dunlin.commands;

import pl.shockah.dunlin.settings.MessageSettingScope;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ChainCommandPattern extends CommandPattern<ChainCommand> {
	public static final String PARAMETERIZED_PATTERN = "^[%s]([^\\s]*?)(?:\\s(.*))?$";
	
	protected final DefaultCommandPattern defaultCommandPattern;
	
	public ChainCommandPattern(DefaultCommandPattern defaultCommandPattern) {
		this.defaultCommandPattern = defaultCommandPattern;
	}
	
	@Override
	public boolean matches(CommandContext context) {
		String content = context.message.getRawContent();
		for (String prefix : defaultCommandPattern.plugin.prefixesSetting.get(new MessageSettingScope(context.message)).split("\\s")) {
			Matcher m = Pattern.compile(String.format(PARAMETERIZED_PATTERN, prefix), Pattern.DOTALL | Pattern.MULTILINE).matcher(content);
			if (m.find() && m.group(1).split(">").length >= 2)
				return true;
		}
		return false;
	}

	@Override
	public CommandPatternMatch<ChainCommand> getCommand(CommandContext context) {
		String commandNamesString = null;
		String input = null;
		String content = context.message.getRawContent();
		for (String prefix : defaultCommandPattern.plugin.prefixesSetting.get(new MessageSettingScope(context.message)).split("\\s")) {
			Matcher m = Pattern.compile(String.format(PARAMETERIZED_PATTERN, prefix), Pattern.DOTALL | Pattern.MULTILINE).matcher(content);
			if (m.find()) {
				commandNamesString = m.group(1);
				input = m.groupCount() >= 2 ? m.group(2) : "";
				break;
			}
		}
		
		Command<?, ?>[] commands = Stream.of(commandNamesString.split(">"))
			.map(commandName ->
				defaultCommandPattern.namedCommandProviders.firstResult(provider ->
					(NamedCommand<?, ?>)provider.provide(context, commandName))).toArray(Command[]::new);
		
		for (Command<?, ?> command : commands) {
			if (command == null)
				return null;
		}
		return new CommandPatternMatch<>(new ChainCommand(commands), input);
	}
}