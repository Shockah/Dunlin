package pl.shockah.dunlin.groovyfactoids;

import pl.shockah.dunlin.factoids.FactoidsPlugin;
import pl.shockah.dunlin.groovyscripting.GroovyScriptingPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.pintail.PluginInfo;

import javax.annotation.Nonnull;

public class GroovyFactoidsPlugin extends Plugin {
	@Nonnull public final GroovyScriptingPlugin scriptingPlugin;
	@Nonnull public final FactoidsPlugin factoidsPlugin;

	@Nonnull private final GroovyFactoidCommandFactory factoidCommandFactory;
	
	public GroovyFactoidsPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info, @Nonnull @RequiredDependency GroovyScriptingPlugin scriptingPlugin, @Nonnull @RequiredDependency FactoidsPlugin factoidsPlugin) {
		super(manager, info);
		this.scriptingPlugin = scriptingPlugin;
		this.factoidsPlugin = factoidsPlugin;

		factoidsPlugin.registerFactory(
				factoidCommandFactory = new GroovyFactoidCommandFactory(scriptingPlugin)
		);
	}

	@Override
	protected void onUnload() {
		factoidsPlugin.unregisterFactory(factoidCommandFactory);
	}
}