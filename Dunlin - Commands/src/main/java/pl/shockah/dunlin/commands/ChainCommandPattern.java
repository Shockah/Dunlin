package pl.shockah.dunlin.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.dv8tion.jda.core.entities.Message;

public class ChainCommandPattern extends CommandPattern<ChainCommand> {
	public static final String PARAMETERIZED_PATTERN = "^[%s]([^\\s]*?)(?:\\s(.*))?$";
	
	protected final DefaultCommandPattern defaultCommandPattern;
	
	public ChainCommandPattern(DefaultCommandPattern defaultCommandPattern) {
		this.defaultCommandPattern = defaultCommandPattern;
	}
	
	@Override
	public boolean matches(Message message) {
		String content = message.getRawContent();
		for (String prefix : defaultCommandPattern.plugin.prefixesSetting.get(message).split("\\s")) {
			Matcher m = Pattern.compile(String.format(PARAMETERIZED_PATTERN, prefix), Pattern.DOTALL | Pattern.MULTILINE).matcher(content);
			if (m.find() && m.group(1).split("\\>").length >= 2)
				return true;
		}
		return false;
	}

	@Override
	public CommandPatternMatch<ChainCommand> getCommand(Message message) {
		String commandNamesString = null;
		String input = null;
		String content = message.getRawContent();
		for (String prefix : defaultCommandPattern.plugin.prefixesSetting.get(message).split("\\s")) {
			Matcher m = Pattern.compile(String.format(PARAMETERIZED_PATTERN, prefix), Pattern.DOTALL | Pattern.MULTILINE).matcher(content);
			if (m.find()) {
				commandNamesString = m.group(1);
				input = m.groupCount() >= 2 ? m.group(2) : "";
				break;
			}
		}
		
		Command<?, ?>[] commands = Stream.of(commandNamesString.split("\\>"))
			.map(commandName ->
				defaultCommandPattern.namedCommandProviders.firstResult(provider ->
					(NamedCommand<?, ?>)provider.provide(commandName))).toArray(size -> new Command<?, ?>[size]);
		
		for (Command<?, ?> command : commands) {
			if (command == null)
				return null;
		}
		return new CommandPatternMatch<>(new ChainCommand(commands), input);
	}
}