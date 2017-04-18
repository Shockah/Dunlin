package pl.shockah.dunlin.settings;

public interface SettingListener<T> {
	void onSettingSet(Setting<T> setting, SettingScope scope, T value);
}