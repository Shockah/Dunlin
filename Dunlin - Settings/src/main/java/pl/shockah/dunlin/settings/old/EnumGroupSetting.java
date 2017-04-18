package pl.shockah.dunlin.settings.old;

import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.settings.SettingsPlugin;

public class EnumGroupSetting<T extends Enum<T>> extends GroupSetting<T> {
	public final Class<T> enumClass;
	
	public EnumGroupSetting(SettingsPlugin settingsPlugin, Class<T> enumClass, Plugin plugin, String name, T defaultValue) {
		super(settingsPlugin, Type.Enum, plugin, name, defaultValue);
		this.enumClass = enumClass;
	}
	
	@Override
	public T get(Scope scope, TextChannel channel) {
		Object raw = getRaw(scope, channel);
		return raw != null ? Enum.valueOf(enumClass, (String)raw) : defaultValue;
	}
	
	@Override
	public void set(T value, Scope scope, TextChannel channel) {
		settingsPlugin.setSettingValueForScope(scope, channel, plugin, name, value.name());
		settingsPlugin.onSettingChange(this);
	}
}