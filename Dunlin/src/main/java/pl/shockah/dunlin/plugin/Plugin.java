package pl.shockah.dunlin.plugin;

import pl.shockah.json.JSONObject;

public class Plugin extends pl.shockah.plugin.Plugin<PluginManager, Plugin> {
	public Plugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	public final JSONObject getConfig() {
		return manager.app.getConfig().getObjectOrEmpty(info.packageName());
	}
}