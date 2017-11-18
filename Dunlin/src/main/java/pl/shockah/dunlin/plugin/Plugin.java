package pl.shockah.dunlin.plugin;

import pl.shockah.json.JSONObject;
import pl.shockah.plugin.PluginInfo;

import javax.annotation.Nonnull;

public class Plugin extends pl.shockah.plugin.Plugin<PluginInfo, PluginManager, Plugin> {
	public Plugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info) {
		super(manager, info);
	}
	
	@Nonnull public final JSONObject getConfig() {
		return manager.app.getConfig().getObjectOrEmpty(info.getPackageName());
	}
}