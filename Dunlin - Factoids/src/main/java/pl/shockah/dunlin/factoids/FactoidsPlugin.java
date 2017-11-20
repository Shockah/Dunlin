package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.pintail.PluginInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FactoidsPlugin extends Plugin {
	@Nonnull protected final CommandsPlugin commandsPlugin;

	@Nonnull public final FactoidCommandProvider commandProvider;
	@Nonnull private final FactoidCommand factoidCommand;
	
	public FactoidsPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info, @Nonnull @RequiredDependency CommandsPlugin commandsPlugin) {
		super(manager, info);
		this.commandsPlugin = commandsPlugin;

		commandsPlugin.registerNamedCommandProvider(
				commandProvider = new FactoidCommandProvider(this)
		);

		commandsPlugin.registerNamedCommand(
				factoidCommand = new FactoidCommand(this)
		);

		registerFactory(new PlainFactoidCommandFactory());
	}

	@Override
	protected void onUnload() {
		commandsPlugin.unregisterNamedCommandProvider(commandProvider);

		commandsPlugin.unregisterNamedCommand(factoidCommand);
	}

	public void registerFactory(@Nonnull FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory) {
		commandProvider.registerFactory(factory);
	}

	public void unregisterFactory(@Nonnull FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory) {
		commandProvider.unregisterFactory(factory);
	}

	@Nullable public Factoid getMatchingFactoid(@Nonnull FactoidScope scope, @Nonnull String name) {
		FactoidScope currentScope = scope;
		while (currentScope != null) {
			Factoid factoid = getFactoid(currentScope, name);
			if (factoid != null)
				return factoid;
			currentScope = currentScope.downscope();
		}
		return null;
	}

	@Nullable public Factoid getFactoid(@Nonnull FactoidScope scope, @Nonnull String name) {
		return scope.getFactoid(this, name);
	}
}