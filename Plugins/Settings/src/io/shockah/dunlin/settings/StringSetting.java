package io.shockah.dunlin.settings;

import io.shockah.dunlin.plugin.Plugin;

public final class StringSetting extends Setting<String> {
	public StringSetting(SettingsPlugin settingsPlugin, Plugin plugin, String name) {
		this(settingsPlugin, plugin, name, null);
	}
	
	public StringSetting(SettingsPlugin settingsPlugin, Plugin plugin, String name, String defaultValue) {
		super(settingsPlugin, plugin, name, defaultValue);
	}

	@Override
	protected String convert(Object o) {
		return null;
	}
}