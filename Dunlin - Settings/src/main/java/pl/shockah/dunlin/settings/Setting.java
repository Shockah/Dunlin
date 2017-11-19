package pl.shockah.dunlin.settings;

import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.util.ReadWriteSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.LinkedHashSet;

public abstract class Setting<T> {
    @Nonnull public final SettingsPlugin settingsPlugin;
    @Nonnull public final Plugin plugin;
    @Nonnull public final String name;
    @Nullable public final T defaultValue;

    @Nonnull protected final ReadWriteSet<SettingListener<T>> listeners = new ReadWriteSet<>(new LinkedHashSet<>());

    protected Setting(@Nonnull SettingsPlugin settingsPlugin, @Nonnull Plugin plugin, @Nonnull String name, @Nullable T defaultValue) {
        this.settingsPlugin = settingsPlugin;
        this.plugin = plugin;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public void registerListener(@Nonnull SettingListener<T> listener) {
        listeners.add(listener);
    }

    public void unregisterListener(@Nonnull SettingListener<T> listener) {
        listeners.remove(listener);
    }

    @Nonnull public final String getFullName() {
        return String.format("%s.%s", plugin.info.getPackageName(), name);
    }

    @Nullable public final T get(@Nonnull SettingScope scope) {
        do {
            T value = getForScope(scope);
            if (value != null)
                return value;
            scope = scope.downscope();
        } while (scope != null);
        return defaultValue;
    }

    public final void set(@Nonnull SettingScope scope, @Nullable T value) {
        setForScope(scope, value);
        settingsPlugin.listeners.iterate(listener -> {
            listener.onSettingSet(getFullName(), scope, value);
        });
        listeners.iterate(listener -> {
            listener.onSettingSet(this, scope, value);
        });
        settingsPlugin.onSettingChange(this);
    }

    @Nullable public abstract T getForScope(@Nonnull SettingScope scope);

    public abstract void setForScope(@Nonnull SettingScope scope, @Nullable T value);

    @Nonnull public abstract T parseValue(@Nonnull String textInput);

    @Nonnull public final PrivateSetting<T> asPrivate() {
        return PrivateSetting.of(this);
    }

    public static Setting<Boolean> ofBool(@Nonnull SettingsPlugin settingsPlugin, @Nonnull Plugin plugin, @Nonnull String name, boolean defaultValue) {
        return new Setting<Boolean>(settingsPlugin, plugin, name, defaultValue) {
            @Override
            @Nullable public Boolean getForScope(@Nonnull SettingScope scope) {
                return (Boolean)scope.getRaw(this);
            }

            @Override
            public void setForScope(@Nonnull SettingScope scope, @Nullable Boolean value) {
                scope.setRaw(this, value);
            }

            @Override
            @Nonnull public Boolean parseValue(@Nonnull String textInput) {
                if (textInput.equalsIgnoreCase("true") || textInput.equalsIgnoreCase("t") || textInput.equalsIgnoreCase("yes") || textInput.equalsIgnoreCase("y"))
                    return true;
                else if (textInput.equalsIgnoreCase("false") || textInput.equalsIgnoreCase("f") || textInput.equalsIgnoreCase("no") || textInput.equalsIgnoreCase("n"))
                    return false;
                else
                    throw new IllegalArgumentException(String.format("Cannot parse `%s` as boolean.", textInput));
            }
        };
    }

    public static Setting<Integer> ofInt(@Nonnull SettingsPlugin settingsPlugin, @Nonnull Plugin plugin, @Nonnull String name, int defaultValue) {
        return new Setting<Integer>(settingsPlugin, plugin, name, defaultValue) {
            @Override
            @Nullable public Integer getForScope(@Nonnull SettingScope scope) {
                return ((BigInteger)scope.getRaw(this)).intValue();
            }

            @Override
            public void setForScope(@Nonnull SettingScope scope, @Nullable Integer value) {
                scope.setRaw(this, value);
            }

            @Override
            @Nonnull public Integer parseValue(@Nonnull String textInput) {
                return new BigInteger(textInput).intValue();
            }
        };
    }

    public static Setting<BigInteger> ofBigInteger(SettingsPlugin settingsPlugin, Plugin plugin, String name, BigInteger defaultValue) {
        return new Setting<BigInteger>(settingsPlugin, plugin, name, defaultValue) {
            @Override
            @Nullable public BigInteger getForScope(@Nonnull SettingScope scope) {
                return (BigInteger)scope.getRaw(this);
            }

            @Override
            public void setForScope(@Nonnull SettingScope scope, @Nullable BigInteger value) {
                scope.setRaw(this, value);
            }

            @Override
            @Nonnull public BigInteger parseValue(@Nonnull String textInput) {
                return new BigInteger(textInput);
            }
        };
    }

    public static Setting<String> ofString(SettingsPlugin settingsPlugin, Plugin plugin, String name, String defaultValue) {
        return new Setting<String>(settingsPlugin, plugin, name, defaultValue) {
            @Override
            @Nullable public String getForScope(@Nonnull SettingScope scope) {
                return (String)scope.getRaw(this);
            }

            @Override
            public void setForScope(@Nonnull SettingScope scope, @Nullable String value) {
                scope.setRaw(this, value);
            }

            @Override
            @Nonnull public String parseValue(@Nonnull String textInput) {
                return textInput;
            }
        };
    }

    @Nonnull public static <E extends Enum<E>> Setting<E> ofEnum(SettingsPlugin settingsPlugin, Plugin plugin, String name, E defaultValue) {
        return new EnumSetting<>(settingsPlugin, plugin, name, defaultValue);
    }

    @Nonnull public static <E extends Enum<E>> Setting<E> ofEnum(SettingsPlugin settingsPlugin, Plugin plugin, String name, Class<E> clazz) {
        return new EnumSetting<>(settingsPlugin, plugin, name, clazz);
    }
}