package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.Scope;

public class PrivateSetting<T> extends Setting<T> {
	protected final Setting<T> baseSetting;
	
	public PrivateSetting(Setting<T> setting) {
		super(setting.settingsPlugin, setting.type, setting.plugin, setting.name, setting.defaultValue);
		baseSetting = setting;
	}
	
	@Override
	public T get(Scope scope, TextChannel channel) {
		throw new SecurityException("Cannot retrieve a private setting.");
	}
}