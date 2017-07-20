package pl.shockah.dunlin.plugin;

import pl.shockah.json.JSONObject;
import pl.shockah.plugin.PluginInfo;

public class Plugin extends pl.shockah.plugin.Plugin<PluginInfo, PluginManager, Plugin> {
	public Plugin(PluginManager manager, PluginInfo info) {
		super(manager, info);
	}
	
	public final JSONObject getConfig() {
		return manager.app.getConfig().getObjectOrEmpty(info.packageName());
	}
}