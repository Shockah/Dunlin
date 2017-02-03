package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.plugin.Plugin;

public final class StringSetting extends Setting<String> {
	public StringSetting(SettingsPlugin settingsPlugin, Plugin plugin, String name) {
		this(settingsPlugin, plugin, name, null);
	}
	
	public StringSetting(SettingsPlugin settingsPlugin, Plugin plugin, String name, String defaultValue) {
		super(settingsPlugin, plugin, name, defaultValue);
	}

	@Override
	public String get(Scope scope, TextChannel channel) {
		try {
			return settingsPlugin.getSettingsObjectForReading(scope, channel, plugin).getString(name);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}