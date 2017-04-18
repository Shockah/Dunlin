package pl.shockah.dunlin.settings;

import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.util.ReadWriteSet;

import java.math.BigInteger;
import java.util.LinkedHashSet;

public abstract class Setting<T> {
    public final SettingsPlugin settingsPlugin;
    public final Plugin plugin;
    public final String name;
    public final T defaultValue;

    protected final ReadWriteSet<SettingListener<T>> listeners = new ReadWriteSet<>(new LinkedHashSet<>());

    protected Setting(SettingsPlugin settingsPlugin, Plugin plugin, String name, T defaultValue) {
        this.settingsPlugin = settingsPlugin;
        this.plugin = plugin;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public void registerListener(SettingListener<T> listener) {
        listeners.add(listener);
    }

    public void unregisterListener(SettingListener<T> listener) {
        listeners.remove(listener);
    }

    public final String getFullName() {
        return String.format("%s.%s", plugin.info.packageName(), name);
    }

    public final T get(SettingScope scope) {
        do {
            T value = getForScope(scope);
            if (value != null)
                return value;
            scope = scope.downscope();
        } while (scope != null);
        return defaultValue;
    }

    public final void set(SettingScope scope, T value) {
        setForScope(scope, value);
        settingsPlugin.listeners.iterate(listener -> {
            listener.onSettingSet(getFullName(), scope, value);
        });
        listeners.iterate(listener -> {
            listener.onSettingSet(this, scope, value);
        });
        settingsPlugin.onSettingChange(this);
    }

    public abstract T getForScope(SettingScope scope);

    public abstract void setForScope(SettingScope scope, T value);

    public abstract T parseValue(String textInput);

    public final PrivateSetting<T> asPrivate() {
        return PrivateSetting.of(this);
    }

    public static Setting<Boolean> ofBool(SettingsPlugin settingsPlugin, Plugin plugin, String name, boolean defaultValue) {
        return new Setting<Boolean>(settingsPlugin, plugin, name, defaultValue) {
            @Override
            public Boolean getForScope(SettingScope scope) {
                return (Boolean)scope.getRaw(this);
            }

            @Override
            public void setForScope(SettingScope scope, Boolean value) {
                scope.setRaw(this, value);
            }

            @Override
            public Boolean parseValue(String textInput) {
                if (textInput.equalsIgnoreCase("true") || textInput.equalsIgnoreCase("t") || textInput.equalsIgnoreCase("yes") || textInput.equalsIgnoreCase("y"))
                    return true;
                else if (textInput.equalsIgnoreCase("false") || textInput.equalsIgnoreCase("f") || textInput.equalsIgnoreCase("no") || textInput.equalsIgnoreCase("n"))
                    return false;
                else
                    throw new IllegalArgumentException(String.format("Cannot parse `%s` as boolean.", textInput));
            }
        };
    }

    public static Setting<Integer> ofInt(SettingsPlugin settingsPlugin, Plugin plugin, String name, int defaultValue) {
        return new Setting<Integer>(settingsPlugin, plugin, name, defaultValue) {
            @Override
            public Integer getForScope(SettingScope scope) {
                return ((BigInteger)scope.getRaw(this)).intValue();
            }

            @Override
            public void setForScope(SettingScope scope, Integer value) {
                scope.setRaw(this, value);
            }

            @Override
            public Integer parseValue(String textInput) {
                return new BigInteger(textInput).intValue();
            }
        };
    }

    public static Setting<BigInteger> ofBigInteger(SettingsPlugin settingsPlugin, Plugin plugin, String name, BigInteger defaultValue) {
        return new Setting<BigInteger>(settingsPlugin, plugin, name, defaultValue) {
            @Override
            public BigInteger getForScope(SettingScope scope) {
                return (BigInteger)scope.getRaw(this);
            }

            @Override
            public void setForScope(SettingScope scope, BigInteger value) {
                scope.setRaw(this, value);
            }

            @Override
            public BigInteger parseValue(String textInput) {
                return new BigInteger(textInput);
            }
        };
    }

    public static Setting<String> ofString(SettingsPlugin settingsPlugin, Plugin plugin, String name, String defaultValue) {
        return new Setting<String>(settingsPlugin, plugin, name, defaultValue) {
            @Override
            public String getForScope(SettingScope scope) {
                return (String)scope.getRaw(this);
            }

            @Override
            public void setForScope(SettingScope scope, String value) {
                scope.setRaw(this, value);
            }

            @Override
            public String parseValue(String textInput) {
                return textInput;
            }
        };
    }

    public static <E extends Enum<E>> Setting<E> ofEnum(SettingsPlugin settingsPlugin, Plugin plugin, String name, E defaultValue, Class<E> clazz) {
        return new EnumSetting<>(settingsPlugin, plugin, name, defaultValue, clazz);
    }
}