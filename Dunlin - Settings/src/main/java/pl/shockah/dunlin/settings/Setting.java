package pl.shockah.dunlin.settings;

import java.math.BigInteger;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.json.JSONObject;

public class Setting<T> {
	public final SettingsPlugin settingsPlugin;
	public final Type type;
	public final Plugin plugin;
	public final String name;
	public final T defaultValue;
	
	protected Setting(SettingsPlugin settingsPlugin, Type type, Plugin plugin, String name, T defaultValue) {
		this.settingsPlugin = settingsPlugin;
		this.type = type;
		this.plugin = plugin;
		this.name = name;
		this.defaultValue = defaultValue;
	}
	
	public static Setting<Boolean> ofBool(SettingsPlugin settingsPlugin, Plugin plugin, String name, boolean defaultValue) {
		return new Setting<>(settingsPlugin, Type.Bool, plugin, name, defaultValue);
	}
	
	public static Setting<String> ofString(SettingsPlugin settingsPlugin, Plugin plugin, String name, String defaultValue) {
		return new Setting<>(settingsPlugin, Type.String, plugin, name, defaultValue);
	}
	
	public static Setting<BigInteger> ofBigInt(SettingsPlugin settingsPlugin, Plugin plugin, String name, BigInteger defaultValue) {
		return new Setting<>(settingsPlugin, Type.Integer, plugin, name, defaultValue);
	}
	
	public static Setting<Integer> ofInt(SettingsPlugin settingsPlugin, Plugin plugin, String name, int defaultValue) {
		return new Setting<Integer>(settingsPlugin, Type.Integer, plugin, name, defaultValue){
			@Override
			public Integer get(Scope scope, TextChannel channel) {
				Object raw = getRaw(scope, channel);
				return raw != null ? ((BigInteger)raw).intValueExact() : defaultValue;
			}
		};
	}
	
	public static Setting<Long> ofLong(SettingsPlugin settingsPlugin, Plugin plugin, String name, long defaultValue) {
		return new Setting<Long>(settingsPlugin, Type.Integer, plugin, name, defaultValue){
			@Override
			public Long get(Scope scope, TextChannel channel) {
				Object raw = getRaw(scope, channel);
				return raw != null ? ((BigInteger)raw).longValueExact() : defaultValue;
			}
		};
	}
	
	public final String getFullName() {
		return String.format("%s.%s", plugin.info.packageName(), name);
	}
	
	public final T get(Message message) {
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
	
	public static enum Type {
		Bool, Integer, Decimal, String, Enum;
	}
}