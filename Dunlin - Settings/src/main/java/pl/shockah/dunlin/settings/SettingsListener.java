package pl.shockah.dunlin.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SettingsListener {
	void onSettingSet(@Nonnull String fullSettingName, @Nonnull SettingScope scope, @Nullable Object value);
}