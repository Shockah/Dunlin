package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.permissions.PermissionsPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.SettingsPlugin;

public class FactoidsPlugin extends Plugin {
	@Dependency
	protected SettingsPlugin settingsPlugin;
	
	@Dependency
	protected CommandsPlugin commandsPlugin;

	@Dependency
	protected PermissionsPlugin permissionsPlugin;

	private FactoidCommandProvider commandProvider;
	
	private FactoidCommand factoidCommand;
	
	public FactoidsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}

	public FactoidCommandProvider getFactoidCommandProvider() {
		return commandProvider;
	}

	public void registerFactory(FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory) {
		commandProvider.registerFactory(factory);
	}

	public void unregisterFactory(FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory) {
		commandProvider.unregisterFactory(factory);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.registerNamedCommandProvider(
				commandProvider = new FactoidCommandProvider(this)
		);

		registerFactory(new PlainFactoidCommandFactory());

		commandsPlugin.registerNamedCommand(
			factoidCommand = new FactoidCommand(this)
		);
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.unregisterNamedCommandProvider(commandProvider);

		commandsPlugin.unregisterNamedCommand(factoidCommand);
	}

	public Factoid getMatchingFactoid(FactoidScope scope, String name) {
		while (scope != null) {
			Factoid factoid = getFactoid(scope, name);
			if (factoid != null)
				return factoid;
			scope = scope.downscope();
		}
		return null;
	}

	public Factoid getFactoid(FactoidScope scope, String name) {
		return scope.getFactoid(this, name);
	}
}