package pl.shockah.dunlin.settings.old;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.util.ReadWriteSet;

import java.math.BigInteger;
import java.util.LinkedHashSet;

public class UserSetting<T> extends Setting<T> {
    protected final ReadWriteSet<UserSettingListener<T>> listeners = new ReadWriteSet<>(new LinkedHashSet<>());

    protected UserSetting(SettingsPlugin settingsPlugin, Type type, Plugin plugin, String name, T defaultValue) {
        super(settingsPlugin, type, plugin, name, defaultValue);
    }

    public static UserSetting<Boolean> ofBool(SettingsPlugin settingsPlugin, Plugin plugin, String name, boolean defaultValue) {
        return new UserSetting<>(settingsPlugin, Type.Bool, plugin, name, defaultValue);
    }

    public static UserSetting<String> ofString(SettingsPlugin settingsPlugin, Plugin plugin, String name, String defaultValue) {
        return new UserSetting<>(settingsPlugin, Type.String, plugin, name, defaultValue);
    }

    public static UserSetting<BigInteger> ofBigInt(SettingsPlugin settingsPlugin, Plugin plugin, String name, BigInteger defaultValue) {
        return new UserSetting<>(settingsPlugin, Type.Integer, plugin, name, defaultValue);
    }

    public static UserSetting<Integer> ofInt(SettingsPlugin settingsPlugin, Plugin plugin, String name, int defaultValue) {
        return new UserSetting<Integer>(settingsPlugin, Type.Integer, plugin, name, defaultValue){
            @Override
            public Integer get(User user) {
                Object raw = getRaw(user);
                return raw != null ? ((BigInteger)raw).intValueExact() : defaultValue;
            }
        };
    }

    public static UserSetting<Long> ofLong(SettingsPlugin settingsPlugin, Plugin plugin, String name, long defaultValue) {
        return new UserSetting<Long>(settingsPlugin, Type.Integer, plugin, name, defaultValue){
            @Override
            public Long get(User user) {
                Object raw = getRaw(user);
                return raw != null ? ((BigInteger)raw).longValueExact() : defaultValue;
            }
        };
    }

    public void registerListener(UserSettingListener<T> listener) {
        listeners.add(listener);
    }

    public void unregisterListener(UserSettingListener<T> listener) {
        listeners.remove(listener);
    }

    @Override
    public T get(Message message) {
        return get(message.getAuthor());
    }

    @SuppressWarnings("unchecked")
    public T get(User user) {
        Object raw = getRaw(user);
        return raw != null ? (T)raw : defaultValue;
    }

    @SuppressWarnings("unchecked")
    protected Object getRaw(User user) {
        return settingsPlugin.getUserSettingValue(user, plugin, name);
    }

    public void set(T value, User user) {
        settingsPlugin.setUserSettingValue(user, plugin, name, value);
        settingsPlugin.onSettingChange(this);
    }
}