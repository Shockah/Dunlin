package pl.shockah.dunlin.plugin;

import pl.shockah.dunlin.App;
import pl.shockah.plugin.PluginInfo;

public class PluginManager extends pl.shockah.plugin.PluginManager<PluginInfo, PluginManager, Plugin> {
	public final App app;
	
	public PluginManager(App app) {
		super(PluginInfo.class, Plugin.class);
		this.app = app;
	}
	
	@Override
	protected void onPluginLoad(Plugin plugin) {
		super.onPluginLoad(plugin);
		
		if (plugin instanceof ListenerPlugin)
			app.getShardManager().eventListenerManager.add(((ListenerPlugin)plugin).listener);
	}
	
	@Override
	protected void onPluginUnload(Plugin plugin) {
		super.onPluginUnload(plugin);
		
		if (plugin instanceof ListenerPlugin)
			app.getShardManager().eventListenerManager.remove(((ListenerPlugin)plugin).listener);
	}
}