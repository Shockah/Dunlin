package io.shockah.dunlin.plugin;

import io.shockah.plugin.PluginManager;

public class Plugin extends io.shockah.plugin.Plugin<Plugin> {
	public Plugin(PluginManager<Plugin> manager, io.shockah.plugin.Plugin.Info info) {
		super(manager, info);
	}
}