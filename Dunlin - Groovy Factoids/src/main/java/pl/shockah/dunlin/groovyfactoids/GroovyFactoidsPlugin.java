package pl.shockah.dunlin.groovyfactoids;

import pl.shockah.dunlin.factoids.FactoidsPlugin;
import pl.shockah.dunlin.groovyscripting.GroovyScriptingPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.plugin.PluginInfo;

public class GroovyFactoidsPlugin extends Plugin {
	@Dependency
	public GroovyScriptingPlugin scriptingPlugin;

	@Dependency
	public FactoidsPlugin factoidsPlugin;

	private GroovyFactoidCommandFactory factoidCommandFactory;
	
	public GroovyFactoidsPlugin(PluginManager manager, PluginInfo info) {
		super(manager, info);
	}

	@Override
	protected void onLoad() {
		factoidsPlugin.registerFactory(
				factoidCommandFactory = new GroovyFactoidCommandFactory(scriptingPlugin)
		);
	}

	@Override
	protected void onUnload() {
		factoidsPlugin.unregisterFactory(factoidCommandFactory);
	}
}