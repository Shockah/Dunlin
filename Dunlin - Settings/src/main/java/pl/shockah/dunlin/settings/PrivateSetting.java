package pl.shockah.dunlin.settings;

public class PrivateSetting<T> extends Setting<T> {
	public final Setting<T> baseSetting;

	public PrivateSetting(Setting<T> baseSetting) {
		super(baseSetting.settingsPlugin, baseSetting.plugin, baseSetting.name, baseSetting.defaultValue);
		this.baseSetting = baseSetting;
	}

	@Override
	public T getForScope(SettingScope scope) {
		return baseSetting.getForScope(scope);
	}

	@Override
	public void setForScope(SettingScope scope, T value) {
		baseSetting.setForScope(scope, value);
	}

	@Override
	public T parseValue(String textInput) {
		return baseSetting.parseValue(textInput);
	}

	public static <T> PrivateSetting<T> of(Setting<T> setting) {
		return new PrivateSetting<>(setting);
	}
}