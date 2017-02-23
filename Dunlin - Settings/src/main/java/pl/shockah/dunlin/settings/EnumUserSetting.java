package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.User;
import pl.shockah.dunlin.plugin.Plugin;

public class EnumUserSetting<T extends Enum<T>> extends UserSetting<T> {
    public final Class<T> enumClass;

    public EnumUserSetting(SettingsPlugin settingsPlugin, Class<T> enumClass, Plugin plugin, String name, T defaultValue) {
        super(settingsPlugin, Type.Enum, plugin, name, defaultValue);
        this.enumClass = enumClass;
    }

    @Override
    public T get(User user) {
        Object raw = getRaw(user);
        return raw != null ? Enum.valueOf(enumClass, (String)raw) : defaultValue;
    }

    @Override
    public void set(T value, User user) {
        settingsPlugin.setUserSettingValue(user, plugin, name, value.name());
        settingsPlugin.onSettingChange(this);
    }
}