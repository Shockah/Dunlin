package io.shockah.dunlin.groovy;

import java.util.LinkedHashMap;
import java.util.Map;
import groovy.lang.Tuple;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.factoids.AbstractFactoidCommand;
import io.shockah.dunlin.factoids.db.Factoid;
import net.dv8tion.jda.events.message.GenericMessageEvent;
import net.dv8tion.jda.events.message.guild.GenericGuildMessageEvent;

public class GroovyFactoidCommand<T, R> extends AbstractFactoidCommand<T, R> {
	public final GroovyPlugin plugin;
	
	public GroovyFactoidCommand(GroovyPlugin plugin, Factoid factoid) {
		super(factoid);
		this.plugin = plugin;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T convertToInput(GenericMessageEvent e, Object input) throws CommandParseException {
		return (T)input;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T parseInput(GenericMessageEvent e, String input) throws CommandParseException {
		return (T)input;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<R> call(CommandCall call, T input) {
		try {
			Map<String, Object> variables = new LinkedHashMap<>();
			variables.put("call", call);
			variables.put("user", call.event.getAuthor());
			if (call.event instanceof GenericGuildMessageEvent) {
				GenericGuildMessageEvent guildMessageEvent = (GenericGuildMessageEvent)call.event;
				variables.put("server", guildMessageEvent.getGuild());
				variables.put("channel", guildMessageEvent.getChannel());
			}
			variables.put("input", input);
			Object result = plugin.getShell(variables, new UserGroovySandboxImpl(), call.event).evaluate(factoid.raw);
			
			if (result instanceof CommandResult<?>)
				return (CommandResult<R>)result;
			
			if (result instanceof Tuple) {
				Tuple tuple = (Tuple)result;
				if (tuple.size() == 2)
					return (CommandResult<R>)CommandResult.of(tuple.get(0), tuple.get(1).toString());
			}
			
			return (CommandResult<R>)CommandResult.of(result);
		} catch (Exception e) {
			return CommandResult.error(e.getMessage());
		}
	}
}