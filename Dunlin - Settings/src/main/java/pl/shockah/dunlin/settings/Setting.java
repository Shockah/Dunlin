package pl.shockah.dunlin.settings;

import pl.shockah.dunlin.plugin.Plugin;

public abstract class Setting<T> {
    public final SettingsPlugin settingsPlugin;
    public final Plugin plugin;
    public final String name;
    public final T defaultValue;

    protected Setting(SettingsPlugin settingsPlugin, Plugin plugin, String name, T defaultValue) {
        this.settingsPlugin = settingsPlugin;
        this.plugin = plugin;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public final String getFullName() {
        return String.format("%s.%s", plugin.info.packageName(), name);
    }

    public abstract T get(SettingScope scope);

    public abstract void set(SettingScope scope, T value);

    protected abstract Object getRaw(SettingScope scope);
}