package io.shockah.dunlin.groovy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import groovy.lang.GroovyShell;
import groovy.lang.Tuple;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.commands.ExceptionCommandResult;
import io.shockah.dunlin.commands.ValueCommandResult;
import io.shockah.dunlin.factoids.AbstractFactoidCommand;
import io.shockah.dunlin.factoids.db.Factoid;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONPrinter;

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
			JSONObject storeData = factoid.getStoreData();
			JSONPrinter printer = new JSONPrinter();
			String jsonOld = storeData == null ? null : printer.toString(storeData);
			
			Map<String, Object> variables = new LinkedHashMap<>();
			variables.put("call", call);
			variables.put("user", call.event.getAuthor());
			if (call.event instanceof GenericGuildMessageEvent) {
				GenericGuildMessageEvent guildMessageEvent = (GenericGuildMessageEvent)call.event;
				variables.put("server", guildMessageEvent.getGuild());
				variables.put("channel", guildMessageEvent.getChannel());
			}
			variables.put("input", input);
			variables.put("store", storeData);
			GroovyShell shell = plugin.getShell(variables, new UserGroovySandboxImpl(), call.event);
			Object result = shell.evaluate(factoid.raw);
			
			if (result instanceof CommandResult<?>)
				return (CommandResult<R>)result;
			
			if (result instanceof Tuple) {
				Tuple tuple = (Tuple)result;
				if (tuple.size() == 2)
					return (CommandResult<R>)new ValueCommandResult<>(tuple.get(0), tuple.get(1).toString());
			}
			
			CommandResult<R> ret = (CommandResult<R>)new ValueCommandResult<>(result);
			JSONObject newStoreData = (JSONObject)plugin.turnIntoJSONValue(shell.getVariable("store"));
			
			String jsonNew = newStoreData == null ? null : printer.toString(newStoreData);
			if (!Objects.equals(jsonOld, jsonNew))
				factoid.setStoreData(newStoreData);
			
			return ret;
		} catch (Exception e) {
			return new ExceptionCommandResult<>(e);
		}
	}
}