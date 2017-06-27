package pl.shockah.dunlin.groovyfactoids;

import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.factoids.FactoidsPlugin;
import pl.shockah.dunlin.groovyscripting.GroovyScriptingPlugin;
import pl.shockah.dunlin.permissions.PermissionsPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;

public class GroovyFactoidsPlugin extends Plugin {
	@Dependency
	public CommandsPlugin commandsPlugin;

	@Dependency
	public PermissionsPlugin permissionsPlugin;

	@Dependency
	public GroovyScriptingPlugin scriptingPlugin;

	@Dependency
	public FactoidsPlugin factoidsPlugin;
	
	public GroovyFactoidsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
}