package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.commands.NamedCompositeCommand;

public class FactoidCommand extends NamedCompositeCommand<Object> {
	public final FactoidsPlugin plugin;

	public final RememberSubcommand rememberSubcommand;

	public FactoidCommand(FactoidsPlugin plugin) {
		super("factoid");
		this.plugin = plugin;

		registerSubcommand(
				rememberSubcommand = new RememberSubcommand(plugin)
		);
	}
}