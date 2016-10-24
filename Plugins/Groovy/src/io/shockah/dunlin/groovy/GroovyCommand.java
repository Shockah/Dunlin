package io.shockah.dunlin.groovy;

import java.util.LinkedHashMap;
import java.util.Map;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.commands.NamedCommand;
import net.dv8tion.jda.events.message.GenericMessageEvent;
import net.dv8tion.jda.events.message.guild.GenericGuildMessageEvent;

public class GroovyCommand extends NamedCommand<String, Object> {
	protected final GroovyPlugin plugin;
	
	public GroovyCommand(GroovyPlugin plugin) {
		super("groovy", "gr");
		this.plugin = plugin;
	}

	@Override
	public String parseInput(GenericMessageEvent e, String input) throws CommandParseException {
		return input;
	}

	@Override
	public CommandResult<Object> call(CommandCall call, String input) {
		try {
			Map<String, Object> variables = new LinkedHashMap<>();
			variables.put("call", call);
			variables.put("user", call.event.getAuthor());
			if (call.event instanceof GenericGuildMessageEvent) {
				GenericGuildMessageEvent guildMessageEvent = (GenericGuildMessageEvent)call.event;
				variables.put("server", guildMessageEvent.getGuild());
				variables.put("channel", guildMessageEvent.getChannel());
			}
			return CommandResult.of(plugin.getShell(variables, new UserGroovySandboxImpl(), call.event).evaluate(input));
		} catch (Exception e) {
			return CommandResult.error(e.getMessage());
		}
	}
}