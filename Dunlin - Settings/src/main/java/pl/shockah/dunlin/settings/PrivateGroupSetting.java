package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.Scope;

public class PrivateGroupSetting<T> extends GroupSetting<T> {
	protected final GroupSetting<T> baseSetting;
	
	public PrivateGroupSetting(GroupSetting<T> setting) {
		super(setting.settingsPlugin, setting.type, setting.plugin, setting.name, setting.defaultValue);
		baseSetting = setting;
	}
	
	@Override
	public T get(Scope scope, TextChannel channel) {
		throw new SecurityException("Cannot retrieve a private setting.");
	}
}