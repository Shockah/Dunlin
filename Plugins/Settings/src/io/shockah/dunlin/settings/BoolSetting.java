package io.shockah.dunlin.settings;

import io.shockah.dunlin.Scope;
import io.shockah.dunlin.plugin.Plugin;
import net.dv8tion.jda.core.entities.TextChannel;

public final class BoolSetting extends Setting<Boolean> {
	public BoolSetting(SettingsPlugin settingsPlugin, Plugin plugin, String name) {
		this(settingsPlugin, plugin, name, null);
	}
	
	public BoolSetting(SettingsPlugin settingsPlugin, Plugin plugin, String name, Boolean defaultValue) {
		super(settingsPlugin, plugin, name, defaultValue);
	}

	@Override
	public void set(Boolean value, Scope scope, TextChannel channel) {
		settingsPlugin.getSettingsObjectForWriting(scope, channel, plugin).put(name, value);
		settingsPlugin.onSettingChange(this);
	}

	@Override
	public Boolean get(Scope scope, TextChannel channel) {
		try {
			return settingsPlugin.getSettingsObjectForReading(scope, channel, plugin).getBool(name);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}