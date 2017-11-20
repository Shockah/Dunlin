package pl.shockah.dunlin.settings.commands;

import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.permissions.PermissionsPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.pintail.PluginInfo;

import javax.annotation.Nonnull;

public class SettingsCommandsPlugin extends Plugin {
	@Nonnull protected final SettingsPlugin settingsPlugin;
	@Nonnull protected final CommandsPlugin commandsPlugin;
	@Nonnull protected final PermissionsPlugin permissionsPlugin;
	
	@Nonnull private final SetCommand setCommand;
	@Nonnull private final GetCommand getCommand;
	
	public SettingsCommandsPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info, @Nonnull @RequiredDependency SettingsPlugin settingsPlugin, @Nonnull @RequiredDependency CommandsPlugin commandsPlugin, @Nonnull @RequiredDependency PermissionsPlugin permissionsPlugin) {
		super(manager, info);
		this.settingsPlugin = settingsPlugin;
		this.commandsPlugin = commandsPlugin;
		this.permissionsPlugin = permissionsPlugin;

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