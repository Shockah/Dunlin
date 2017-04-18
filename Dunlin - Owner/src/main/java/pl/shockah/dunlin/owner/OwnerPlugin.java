package pl.shockah.dunlin.owner;

import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.db.DatabaseManager;
import pl.shockah.dunlin.permissions.PermissionsPlugin;
import pl.shockah.dunlin.permissions.db.PermissionGroup;
import pl.shockah.dunlin.permissions.db.PermissionUser;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.old.GroupSetting;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.json.JSONList;

public class OwnerPlugin extends Plugin {
	@Dependency
	public CommandsPlugin commandsPlugin;

	@Dependency
	public PermissionsPlugin permissionsPlugin;

	@Dependency
	public SettingsPlugin settingsPlugin;

	private ReloadCommand reloadCommand;
	private PingCommand pingCommand;
	private AnnounceCommand announceCommand;
	private StatusCommand statusCommand;
	private UserCommand userCommand;

	protected GroupSetting<String> announceChannelSetting;
	
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

		commandsPlugin.registerNamedCommand(
				reloadCommand = new ReloadCommand(this)
		);
		commandsPlugin.registerNamedCommand(
				pingCommand = new PingCommand()
		);
		commandsPlugin.registerNamedCommand(
				announceCommand = new AnnounceCommand(this)
		);
		commandsPlugin.registerNamedCommand(
				statusCommand = new StatusCommand(this)
		);
		commandsPlugin.registerNamedCommand(
				userCommand = new UserCommand(this)
		);

		settingsPlugin.register(
				announceChannelSetting = GroupSetting.ofString(settingsPlugin, this, "announceChannel", ".")
		);
	}

	@Override
	protected void onUnload() {
		commandsPlugin.unregisterNamedCommand(reloadCommand);
		commandsPlugin.unregisterNamedCommand(pingCommand);
		commandsPlugin.unregisterNamedCommand(announceCommand);
		commandsPlugin.unregisterNamedCommand(statusCommand);
		commandsPlugin.unregisterNamedCommand(userCommand);

		settingsPlugin.unregister(announceChannelSetting);
	}
}