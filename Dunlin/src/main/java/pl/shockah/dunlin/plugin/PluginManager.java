package pl.shockah.dunlin.plugin;

import pl.shockah.dunlin.App;

public class PluginManager extends pl.shockah.plugin.PluginManager<Plugin> {
	public final App app;
	
	public PluginManager(App app) {
		this(Plugin.class, app);
	}
	
	protected PluginManager(Class<Plugin> clazz, App app) {
		super(clazz);
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