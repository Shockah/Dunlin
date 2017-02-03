package pl.shockah.dunlin.commands;

import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.dunlin.settings.StringSetting;

public class CommandsPlugin extends ListenerPlugin {
	@Dependency
	private SettingsPlugin settingsPlugin;
	
	protected StringSetting prefixesSetting;
	
	public CommandsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		prefixesSetting = new StringSetting(settingsPlugin, this, "prefixes", ".");
	}
}