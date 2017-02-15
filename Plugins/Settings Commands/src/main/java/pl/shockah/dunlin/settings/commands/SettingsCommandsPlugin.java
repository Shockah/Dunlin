package pl.shockah.dunlin.settings.commands;

import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.SettingsPlugin;

public class SettingsCommandsPlugin extends Plugin {
	@Dependency
	protected SettingsPlugin settingsPlugin;
	
	@Dependency
	protected CommandsPlugin commandsPlugin;
	
	private SetCommand setCommand;
	private GetCommand getCommand;
	
	public SettingsCommandsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.registerNamedCommand(
			setCommand = new SetCommand(settingsPlugin)
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