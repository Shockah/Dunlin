package io.shockah.dunlin.settings;

import io.shockah.dunlin.Scope;
import io.shockah.dunlin.plugin.Plugin;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class Setting<T> {
	public final SettingsPlugin settingsPlugin;
	public final Plugin plugin;
	public final String name;
	public final T defaultValue;
	
	Setting(SettingsPlugin settingsPlugin, Plugin plugin, String name, T defaultValue) {
		this.settingsPlugin = settingsPlugin;
		this.plugin = plugin;
		this.name = name;
		this.defaultValue = defaultValue;
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
	
	public abstract void set(T value, Scope scope, TextChannel channel);
	
	public abstract T get(Scope scope, TextChannel channel);
}