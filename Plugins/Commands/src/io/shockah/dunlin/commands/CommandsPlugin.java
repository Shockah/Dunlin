package io.shockah.dunlin.commands;

import java.util.ArrayList;
import java.util.Arrays;
import io.shockah.dunlin.DelegatePassthroughException;
import io.shockah.dunlin.plugin.ListenerPlugin;
import io.shockah.dunlin.plugin.PluginManager;
import io.shockah.dunlin.util.ReadWriteList;
import io.shockah.json.JSONList;
import io.shockah.util.UnexpectedException;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class CommandsPlugin extends ListenerPlugin {
	protected ReadWriteList<CommandPattern> patterns = new ReadWriteList<>(new ArrayList<>());
	
	protected DefaultCommandPattern defaultPattern;
	protected DefaultCommandProvider defaultProvider;
	
	public CommandsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		getConfig().putDefault("prefixes", JSONList.of(".", "`"));
		addPattern(defaultPattern = new DefaultCommandPattern(getConfig().getList("prefixes").ofStrings().toArray(new String[0])));
		addProvider(defaultProvider = new DefaultCommandProvider());
		addNamedCommand(new ToStringCommand());
	}
	
	public DefaultCommandPattern getDefaultPattern() {
		return defaultPattern;
	}
	
	public DefaultCommandProvider getDefaultProvider() {
		return defaultProvider;
	}
	
	public void addPattern(CommandPattern pattern) {
		patterns.add(pattern);
	}
	
	public void removePattern(CommandPattern pattern) {
		patterns.remove(pattern);
	}
	
	public void addProvider(CommandProvider provider) {
		defaultPattern.addProvider(provider);
	}
	
	public void removeProvider(CommandProvider provider) {
		defaultPattern.removeProvider(provider);
	}
	
	public void addNamedCommand(NamedCommand<?, ?> command) {
		defaultProvider.addNamedCommand(command);
	}
	
	public void addNamedCommands(NamedCommand<?, ?>... commands) {
		for (NamedCommand<?, ?> command : commands) {
			addNamedCommand(command);
		}
	}
	
	public void removeNamedCommand(NamedCommand<?, ?> command) {
		defaultProvider.removeNamedCommand(command);
	}
	
	public void removeNamedCommands(NamedCommand<?, ?>... commands) {
		for (NamedCommand<?, ?> command : commands) {
			removeNamedCommand(command);
		}
	}
	
	public PreparedCommandCall<?, ?> findCommandToCall(GenericMessageEvent event) throws CommandParseException {
		try {
			return patterns.firstResult(pattern -> {
				try {
					return pattern.provide(event);
				} catch (Exception e) {
					throw new DelegatePassthroughException(e);
				}
			});
		} catch (DelegatePassthroughException ex) {
			if (ex.getCause() instanceof CommandParseException)
				throw new CommandParseException(ex.getCause());
			throw new UnexpectedException(ex);
		}
	}
	
	public NamedCommand<?, ?> findCommand(GenericMessageEvent event, String name) {
		return defaultPattern.findCommand(event, name);
	}
	
	@Override
	protected void onGenericMessage(GenericMessageEvent e) {
		if (e.getAuthor() == e.getJDA().getSelfUser())
			return;
		if (!(e instanceof GuildMessageReceivedEvent || e instanceof PrivateMessageReceivedEvent))
			return;
		
		CommandCall call = new CommandCall(e);
		try {
			PreparedCommandCall<?, ?> preparedCall = findCommandToCall(e);
			if (preparedCall == null)
				return;
			
			CommandResult<?> value = preparedCall.call(call);
			String output = value.toString();
			
			if (output != null)
				call.respond(output);
		} catch (CommandParseException ex) {
			call.respond(Arrays.asList(new String[] { ex.getMessage() }));
		}
	}
}