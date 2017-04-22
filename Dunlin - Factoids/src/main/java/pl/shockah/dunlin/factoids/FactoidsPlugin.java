package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.commands.CommandsPlugin;
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
	
	//private RememberCommand rememberCommand;
	//private ForgetCommand forgetCommand;
	
	public FactoidsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		/*commandsPlugin.registerNamedCommand(
			setCommand = new SetCommand(this)
		);
		commandsPlugin.registerNamedCommand(
			getCommand = new GetCommand(settingsPlugin)
		);*/
	}
	
	@Override
	protected void onUnload() {
		//commandsPlugin.unregisterNamedCommand(setCommand);
		//commandsPlugin.unregisterNamedCommand(getCommand);
	}
}