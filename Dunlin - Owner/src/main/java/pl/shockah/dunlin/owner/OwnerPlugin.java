package pl.shockah.dunlin.owner;

import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.db.DatabaseManager;
import pl.shockah.dunlin.permissions.db.PermissionGroup;
import pl.shockah.dunlin.permissions.db.PermissionUser;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.json.JSONList;

public class OwnerPlugin extends Plugin {
	@Dependency
	private CommandsPlugin commandsPlugin;
	
	public OwnerPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		DatabaseManager db = manager.app.getDatabaseManager();
		if (db.selectFirst(PermissionGroup.class, q -> q.where().eq(PermissionGroup.NAME, "owner")) == null) {
			PermissionGroup group = db.create(PermissionGroup.class, obj -> {
				obj.setName("owner");
				obj.setPermissions(JSONList.of("*"));
			});
			
			db.create(PermissionUser.class, obj -> {
				obj.setUserId(getConfig().getString("ownerUserId"));
				obj.setGroup(group);
			});
		}
	}
}