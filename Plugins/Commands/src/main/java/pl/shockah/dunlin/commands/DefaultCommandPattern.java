package pl.shockah.dunlin.commands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.util.ReadWriteList;

public class DefaultCommandPattern<T extends NamedCommand<?, ?>> extends CommandPattern<T> {
	public static final String PARAMETERIZED_PATTERN = "^[%s](.*?)(?:\\s(.*))$";
	
	private final CommandsPlugin plugin;
	protected final ReadWriteList<NamedCommandProvider<?, ?>> namedCommandProviders = new ReadWriteList<>(new ArrayList<>());
	
	public DefaultCommandPattern(CommandsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean matches(Message message) {
		String content = message.getRawContent();
		for (String prefix : plugin.prefixesSetting.get(message).split("\\s")) {
			if (content.matches(String.format(PARAMETERIZED_PATTERN, prefix)))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getCommand(Message message) {
		String commandName = null;
		//String input = null;
		String content = message.getRawContent();
		for (String prefix : plugin.prefixesSetting.get(message).split("\\s")) {
			Matcher m = Pattern.compile(String.format(PARAMETERIZED_PATTERN, prefix)).matcher(content);
			if (m.find()) {
				commandName = m.group(1);
				//input = m.group(2);
				break;
			}
		}
		
		final String f_commandName = commandName;
		return namedCommandProviders.firstResult(provider -> {
			return (T)provider.provide(f_commandName);
		});
	}
	
	public void register(NamedCommandProvider<?, ?> namedCommandProvider) {
		namedCommandProviders.add(namedCommandProvider);
	}
	
	public void unregister(NamedCommandProvider<?, ?> namedCommandProvider) {
		namedCommandProviders.remove(namedCommandProvider);
	}
}