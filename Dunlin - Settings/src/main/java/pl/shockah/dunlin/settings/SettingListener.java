package pl.shockah.dunlin.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SettingListener<T> {
	void onSettingSet(@Nonnull Setting<T> setting, @Nonnull SettingScope scope, @Nullable T value);
}