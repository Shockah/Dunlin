package io.shockah.dunlin.factoids;

import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandProvider;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.factoids.db.Factoid;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public class FactoidCommandProvider extends CommandProvider {
	protected final FactoidsPlugin plugin;
	
	public FactoidCommandProvider(FactoidsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public NamedCommand<?, ?> provide(GenericMessageEvent e, String commandName) {
		Factoid factoid = plugin.findActiveFactoid(e, commandName);
		if (factoid == null)
			return null;
		
		FactoidType type = plugin.getType(factoid.type);
		if (type == null) {
			return new NamedCommand<Void, Void>(commandName) {
				@Override
				public Void parseInput(GenericMessageEvent e, String input) throws CommandParseException {
					return null;
				}

				@Override
				public CommandResult<Void> call(CommandCall call, Void input) {
					return CommandResult.error(String.format("Unknown factoid type %s.", factoid.type));
				}
			};
		}
		
		return type.createCommand(factoid);
	}
}