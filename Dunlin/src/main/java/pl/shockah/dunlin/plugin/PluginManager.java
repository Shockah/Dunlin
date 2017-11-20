package pl.shockah.dunlin.plugin;

import pl.shockah.dunlin.App;
import pl.shockah.pintail.PluginInfo;

import javax.annotation.Nonnull;

public class PluginManager extends pl.shockah.pintail.PluginManager<PluginInfo, PluginManager, Plugin> {
	@Nonnull public final App app;
	
	public PluginManager(@Nonnull App app) {
		super(PluginInfo.class, Plugin.class);
		this.app = app;
	}
	
	@Override
	protected void onPluginLoad(@Nonnull Plugin plugin) {
		super.onPluginLoad(plugin);
		
		if (plugin instanceof ListenerPlugin)
			app.getShardManager().eventListenerManager.add(((ListenerPlugin)plugin).listener);
	}
	
	@Override
	protected void onPluginUnload(@Nonnull Plugin plugin) {
		super.onPluginUnload(plugin);
		
		if (plugin instanceof ListenerPlugin)
			app.getShardManager().eventListenerManager.remove(((ListenerPlugin)plugin).listener);
	}
}