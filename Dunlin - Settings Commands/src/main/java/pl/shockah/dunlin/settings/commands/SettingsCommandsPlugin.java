package pl.shockah.dunlin.settings.commands;

import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.permissions.PermissionsPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.plugin.PluginInfo;

import javax.annotation.Nonnull;

public class SettingsCommandsPlugin extends Plugin {
	@Dependency
	protected SettingsPlugin settingsPlugin;
	
	@Dependency
	protected CommandsPlugin commandsPlugin;

	@Dependency
	protected PermissionsPlugin permissionsPlugin;
	
	private SetCommand setCommand;
	private GetCommand getCommand;
	
	public SettingsCommandsPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.registerNamedCommand(
			setCommand = new SetCommand(this)
		);
		commandsPlugin.registerNamedCommand(
			getCommand = new GetCommand(settingsPlugin)
		);
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.unregisterNamedCommand(setCommand);
		commandsPlugin.unregisterNamedCommand(getCommand);
	}
}