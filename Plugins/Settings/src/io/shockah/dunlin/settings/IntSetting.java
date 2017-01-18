package io.shockah.dunlin.settings;

import io.shockah.dunlin.Scope;
import io.shockah.dunlin.plugin.Plugin;
import net.dv8tion.jda.core.entities.TextChannel;

public final class IntSetting extends Setting<Integer> {
	public IntSetting(SettingsPlugin settingsPlugin, Plugin plugin, String name) {
		this(settingsPlugin, plugin, name, null);
	}
	
	public IntSetting(SettingsPlugin settingsPlugin, Plugin plugin, String name, Integer defaultValue) {
		super(settingsPlugin, plugin, name, defaultValue);
	}

	@Override
	public void set(Integer value, Scope scope, TextChannel channel) {
		settingsPlugin.getSettingsObjectForWriting(scope, channel, plugin).put(name, value);
		settingsPlugin.onSettingChange(this);
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