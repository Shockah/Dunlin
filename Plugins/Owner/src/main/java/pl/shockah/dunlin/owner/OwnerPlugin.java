package pl.shockah.dunlin.owner;

import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.plugin.PluginManager;

public class OwnerPlugin extends Plugin {
	@Dependency
	private CommandsPlugin commandsPlugin;
	
	public OwnerPlugin(PluginManager<Plugin> manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		//if (Ebean.find(PermissionGroup.class).where().eq("name", "owner").findCount() == 0) {
			//TODO: create
		//}
	}
}