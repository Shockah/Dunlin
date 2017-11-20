package pl.shockah.dunlin.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrivateSetting<T> extends Setting<T> {
	@Nonnull public final Setting<T> baseSetting;

	public PrivateSetting(@Nonnull Setting<T> baseSetting) {
		super(baseSetting.settingsPlugin, baseSetting.plugin, baseSetting.name, baseSetting.defaultValue);
		this.baseSetting = baseSetting;
	}

	@Override
	@Nullable public T getForScope(@Nonnull SettingScope scope) {
		return baseSetting.getForScope(scope);
	}

	@Override
	public void setForScope(@Nonnull SettingScope scope, @Nullable T value) {
		baseSetting.setForScope(scope, value);
	}

	@Override
	@Nonnull public T parseValue(@Nonnull String textInput) {
		return baseSetting.parseValue(textInput);
	}

	public static <T> PrivateSetting<T> of(Setting<T> setting) {
		return new PrivateSetting<>(setting);
	}
}