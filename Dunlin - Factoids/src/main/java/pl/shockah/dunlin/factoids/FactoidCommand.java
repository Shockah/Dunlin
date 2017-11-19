package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.commands.NamedCompositeCommand;

import javax.annotation.Nonnull;

public class FactoidCommand extends NamedCompositeCommand {
	@Nonnull public final FactoidsPlugin plugin;

	@Nonnull public final RememberSubcommand rememberSubcommand;
	@Nonnull public final ForgetSubcommand forgetSubcommand;
	@Nonnull public final InfoSubcommand infoSubcommand;

	public FactoidCommand(@Nonnull FactoidsPlugin plugin) {
		super("factoid");
		this.plugin = plugin;

		registerSubcommand(
				rememberSubcommand = new RememberSubcommand(plugin)
		);
		registerSubcommand(
				forgetSubcommand = new ForgetSubcommand(plugin)
		);
		registerSubcommand(
				infoSubcommand = new InfoSubcommand(plugin)
		);
	}
}