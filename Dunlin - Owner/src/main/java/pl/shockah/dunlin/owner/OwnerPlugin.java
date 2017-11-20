package pl.shockah.dunlin.owner;

import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.db.DatabaseManager;
import pl.shockah.dunlin.permissions.PermissionsPlugin;
import pl.shockah.dunlin.permissions.db.PermissionGroup;
import pl.shockah.dunlin.permissions.db.PermissionUser;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.Setting;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.jay.JSONList;
import pl.shockah.pintail.PluginInfo;

import javax.annotation.Nonnull;

public class OwnerPlugin extends Plugin {
	@Nonnull public final CommandsPlugin commandsPlugin;
	@Nonnull public final PermissionsPlugin permissionsPlugin;
	@Nonnull public final SettingsPlugin settingsPlugin;

	@Nonnull private final ReloadCommand reloadCommand;
	@Nonnull private final PingCommand pingCommand;
	@Nonnull private final AnnounceCommand announceCommand;
	@Nonnull private final StatusCommand statusCommand;
	@Nonnull private final UserCommand userCommand;

	@Nonnull protected final Setting<String> announceChannelSetting;
	
	public OwnerPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info, @Nonnull @RequiredDependency CommandsPlugin commandsPlugin, @Nonnull @RequiredDependency PermissionsPlugin permissionsPlugin, @Nonnull @RequiredDependency SettingsPlugin settingsPlugin) {
		super(manager, info);
		this.commandsPlugin = commandsPlugin;
		this.permissionsPlugin = permissionsPlugin;
		this.settingsPlugin = settingsPlugin;

		DatabaseManager db = manager.app.getDatabaseManager();
		if (db.selectFirst(PermissionGroup.class, q -> q.where().eq(PermissionGroup.NAME, "owner")) == null) {
			PermissionGroup group = db.create(PermissionGroup.class, obj -> {
				obj.setName("owner");
				obj.setPermissions(JSONList.of("*"));
			});

			db.create(PermissionUser.class, obj -> {
				obj.setUserId(getConfig().getLong("ownerUserId"));
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
				announceChannelSetting = Setting.ofString(settingsPlugin, this, "announceChannel", ".")
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