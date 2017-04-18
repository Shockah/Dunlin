package pl.shockah.dunlin.settings.old;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.settings.SettingsPlugin;

public abstract class Setting<T> {
    public final SettingsPlugin settingsPlugin;
    public final GroupSetting.Type type;
    public final Plugin plugin;
    public final String name;
    public final T defaultValue;

    protected Setting(SettingsPlugin settingsPlugin, GroupSetting.Type type, Plugin plugin, String name, T defaultValue) {
        this.settingsPlugin = settingsPlugin;
        this.type = type;
        this.plugin = plugin;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public final String getFullName() {
        return String.format("%s.%s", plugin.info.packageName(), name);
    }

    public abstract T get(Message message);
}