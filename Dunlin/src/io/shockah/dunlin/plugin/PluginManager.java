package io.shockah.dunlin.plugin;

import io.shockah.dunlin.App;

public class PluginManager extends io.shockah.plugin.PluginManager<Plugin> {
	public final App app;
	
	public PluginManager(App app) {
		this.app = app;
	}
	
	@Override
	protected void onPluginLoad(Plugin plugin) {
		super.onPluginLoad(plugin);
		
		if (plugin instanceof ListenerPlugin)
			app.getListenerManager().add(((ListenerPlugin)plugin).listener);
	}
	
	@Override
	protected void onPluginUnload(Plugin plugin) {
		super.onPluginUnload(plugin);
		
		if (plugin instanceof ListenerPlugin)
			app.getListenerManager().remove(((ListenerPlugin)plugin).listener);
	}
}