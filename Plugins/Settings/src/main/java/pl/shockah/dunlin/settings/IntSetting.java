package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.plugin.Plugin;

public final class IntSetting extends Setting<Integer> {
	public IntSetting(SettingsPlugin settingsPlugin, Plugin plugin, String name) {
		this(settingsPlugin, plugin, name, null);
	}
	
	public IntSetting(SettingsPlugin settingsPlugin, Plugin plugin, String name, Integer defaultValue) {
		super(settingsPlugin, plugin, name, defaultValue);
	}

	@Override
	public Integer get(Scope scope, TextChannel channel) {
		try {
			return settingsPlugin.getSettingsObjectForReading(scope, channel, plugin).getInt(name);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}