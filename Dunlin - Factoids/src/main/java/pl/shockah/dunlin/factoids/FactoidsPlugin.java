package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.plugin.PluginInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FactoidsPlugin extends Plugin {
	@Dependency
	protected CommandsPlugin commandsPlugin;

	private FactoidCommandProvider commandProvider;
	
	private FactoidCommand factoidCommand;
	
	public FactoidsPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info) {
		super(manager, info);
	}

	@Nonnull public FactoidCommandProvider getFactoidCommandProvider() {
		if (commandProvider == null)
			throw new IllegalStateException();
		return commandProvider;
	}

	public void registerFactory(@Nonnull FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory) {
		commandProvider.registerFactory(factory);
	}

	public void unregisterFactory(@Nonnull FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory) {
		commandProvider.unregisterFactory(factory);
	}
	
	@Override
	protected void onLoad() {
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