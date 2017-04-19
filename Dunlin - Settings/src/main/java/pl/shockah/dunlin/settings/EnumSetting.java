package pl.shockah.dunlin.settings;

import pl.shockah.dunlin.plugin.Plugin;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {
	public final Class<T> clazz;

	public EnumSetting(SettingsPlugin settingsPlugin, Plugin plugin, String name, T defaultValue, Class<T> clazz) {
		super(settingsPlugin, plugin, name, defaultValue);
		this.clazz = clazz;
	}

	@Override
	public T getForScope(SettingScope scope) {
		String raw = (String)scope.getRaw(this);
		if (raw == null)
			return null;
		return Enum.valueOf(clazz, raw);
	}

	@Override
	public void setForScope(SettingScope scope, T value) {
		scope.setRaw(this, value.name());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T parseValue(String textInput) {
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
		} catch (NumberFormatException e2) {
		}

		throw new IllegalArgumentException(String.format("Cannot parse `%s` as enum `%s`.", textInput, clazz.getSimpleName()));
	}
}