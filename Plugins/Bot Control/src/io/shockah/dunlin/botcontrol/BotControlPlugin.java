package io.shockah.dunlin.botcontrol;

import io.shockah.dunlin.commands.CommandsPlugin;
import io.shockah.dunlin.permissions.PermissionsPlugin;
import io.shockah.dunlin.plugin.ListenerPlugin;
import io.shockah.dunlin.plugin.PluginManager;

public class BotControlPlugin extends ListenerPlugin {
	@Dependency
	protected CommandsPlugin commandsPlugin;
	
	@Dependency
	protected PermissionsPlugin permissionsPlugin;
	
	private ReloadPluginsCommand reloadPluginsCommand;
	
	public BotControlPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.addNamedCommands(
			reloadPluginsCommand = new ReloadPluginsCommand(this)
		);
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeNamedCommands(
			reloadPluginsCommand
		);
	}
	
	public ReloadPluginsCommand getReloadPluginsCommand() {
		return reloadPluginsCommand;
	}
}