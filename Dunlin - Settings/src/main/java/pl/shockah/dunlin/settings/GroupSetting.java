package pl.shockah.dunlin.settings;

import java.math.BigInteger;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.plugin.Plugin;

public class GroupSetting<T> extends Setting<T> {
	protected GroupSetting(SettingsPlugin settingsPlugin, Type type, Plugin plugin, String name, T defaultValue) {
		super(settingsPlugin, type, plugin, name, defaultValue);
	}
	
	public static GroupSetting<Boolean> ofBool(SettingsPlugin settingsPlugin, Plugin plugin, String name, boolean defaultValue) {
		return new GroupSetting<>(settingsPlugin, Type.Bool, plugin, name, defaultValue);
	}
	
	public static GroupSetting<String> ofString(SettingsPlugin settingsPlugin, Plugin plugin, String name, String defaultValue) {
		return new GroupSetting<>(settingsPlugin, Type.String, plugin, name, defaultValue);
	}
	
	public static GroupSetting<BigInteger> ofBigInt(SettingsPlugin settingsPlugin, Plugin plugin, String name, BigInteger defaultValue) {
		return new GroupSetting<>(settingsPlugin, Type.Integer, plugin, name, defaultValue);
	}
	
	public static GroupSetting<Integer> ofInt(SettingsPlugin settingsPlugin, Plugin plugin, String name, int defaultValue) {
		return new GroupSetting<Integer>(settingsPlugin, Type.Integer, plugin, name, defaultValue){
			@Override
			public Integer get(Scope scope, TextChannel channel) {
				Object raw = getRaw(scope, channel);
				return raw != null ? ((BigInteger)raw).intValueExact() : defaultValue;
			}
		};
	}
	
	public static GroupSetting<Long> ofLong(SettingsPlugin settingsPlugin, Plugin plugin, String name, long defaultValue) {
		return new GroupSetting<Long>(settingsPlugin, Type.Integer, plugin, name, defaultValue){
			@Override
			public Long get(Scope scope, TextChannel channel) {
				Object raw = getRaw(scope, channel);
				return raw != null ? ((BigInteger)raw).longValueExact() : defaultValue;
			}
		};
	}

	@Override
	public T get(Message message) {
		TextChannel channel = message.getTextChannel();
		
		if (message.getGuild() != null) {
			T value = get(Scope.Channel, channel);
			if (value != null)
				return value;
			
			value = get(Scope.Server, channel);
			if (value != null)
				return value;
		}
		
		T value = get(Scope.Global, channel);
		if (value != null)
			return value;
		
		return defaultValue;
	}
	
	@SuppressWarnings("unchecked")
	public T get(Scope scope, TextChannel channel) {
		Object raw = getRaw(scope, channel);
		return raw != null ? (T)raw : defaultValue;
	}

	@SuppressWarnings("unchecked")
	protected Object getRaw(Scope scope, TextChannel channel) {
		return settingsPlugin.getSettingValue(scope, channel, plugin, name);
	}
	
	public void set(T value, Scope scope, TextChannel channel) {
		settingsPlugin.setSettingValueForScope(scope, channel, plugin, name, value);
		settingsPlugin.onSettingChange(this);
	}
}