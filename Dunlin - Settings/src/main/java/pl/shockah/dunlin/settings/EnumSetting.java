package pl.shockah.dunlin.settings;

import pl.shockah.dunlin.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {
	@Nonnull public final Class<T> clazz;

	@SuppressWarnings("unchecked")
	public EnumSetting(@Nonnull SettingsPlugin settingsPlugin, @Nonnull Plugin plugin, @Nonnull String name, @Nonnull T defaultValue) {
		super(settingsPlugin, plugin, name, defaultValue);
		clazz = (Class<T>)defaultValue.getClass();
	}

	public EnumSetting(@Nonnull SettingsPlugin settingsPlugin, @Nonnull Plugin plugin, @Nonnull String name, @Nonnull Class<T> clazz) {
		super(settingsPlugin, plugin, name, null);
		this.clazz = clazz;
	}

	@Override
	@Nullable
	public T getForScope(@Nonnull SettingScope scope) {
		String raw = (String)scope.getRaw(this);
		if (raw == null)
			return null;
		return Enum.valueOf(clazz, raw);
	}

	@Override
	public void setForScope(@Nonnull SettingScope scope, @Nullable T value) {
		if (value == null)
			throw new IllegalArgumentException();
		scope.setRaw(this, value.name());
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull public T parseValue(@Nonnull String textInput) {
		for (Object obj : clazz.getEnumConstants()) {
			Enum<?> enumConst = (Enum<?>)obj;
			if (enumConst.name().equalsIgnoreCase(textInput)) {
				return (T)enumConst;
			}
		}

		try {
			int ordinal = Integer.parseInt(textInput);
			for (Object obj : clazz.getEnumConstants()) {
				Enum<?> enumConst = (Enum<?>)obj;
				if (enumConst.ordinal() == ordinal) {
					return (T)enumConst;
				}
			}
		} catch (NumberFormatException ignored) {
		}

		throw new IllegalArgumentException(String.format("Cannot parse `%s` as enum `%s`.", textInput, clazz.getSimpleName()));
	}
}