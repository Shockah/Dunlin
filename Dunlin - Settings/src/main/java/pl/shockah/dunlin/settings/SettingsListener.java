package pl.shockah.dunlin.settings;

public interface SettingsListener {
	void onSettingSet(String fullSettingName, SettingScope scope, Object value);
}