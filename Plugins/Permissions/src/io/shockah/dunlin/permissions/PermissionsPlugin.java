package io.shockah.dunlin.permissions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.j256.ormlite.stmt.QueryBuilder;
import io.shockah.dunlin.db.DatabaseManager;
import io.shockah.dunlin.permissions.db.UserGroup;
import io.shockah.dunlin.permissions.db.UserGroupChannelPermission;
import io.shockah.dunlin.permissions.db.UserGroupPermission;
import io.shockah.dunlin.permissions.db.UserGroupRole;
import io.shockah.dunlin.permissions.db.UserGroupUser;
import io.shockah.dunlin.plugin.Plugin;
import io.shockah.dunlin.plugin.PluginManager;
import io.shockah.util.UnexpectedException;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class PermissionsPlugin extends Plugin {
	public PermissionsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	public List<UserGroup> getUserGroups(User user) {
		return getUserGroups(user, null, null);
	}
	
	public List<UserGroup> getUserGroups(User user, Guild guild) {
		return getUserGroups(user, null, guild);
	}
	
	public List<UserGroup> getUserGroups(User user, TextChannel channel) {
		return getUserGroups(user, channel, channel.getGuild());
	}
	
	private List<UserGroup> getUserGroups(User user, TextChannel channel, Guild guild) {
		try {
			DatabaseManager databaseManager = manager.app.getDatabaseManager();
			
			Set<UserGroup> userGroups = new HashSet<>();
			
			if (guild != null) {
				for (Role role : channel.getGuild().getMember(user).getRoles()) {
					QueryBuilder<UserGroupRole, Integer> qbRole = databaseManager.getDao(UserGroupRole.class, Integer.class).queryBuilder();
					qbRole.where().eq(UserGroupRole.ROLE_COLUMN, role.getId());
					
					QueryBuilder<UserGroup, Integer> qbGroup = databaseManager.getDao(UserGroup.class, Integer.class).queryBuilder();
					userGroups.addAll(qbGroup.join(qbRole).query());
				}
			}
			
			if (channel != null) {
				for (Permission permission : Permission.values()) {
					if (channel.getGuild().getMember(user).hasPermission(permission)) {
						QueryBuilder<UserGroupChannelPermission, Integer> qbChannelPermission = databaseManager.getDao(UserGroupChannelPermission.class, Integer.class).queryBuilder();
						qbChannelPermission.where().eq(UserGroupChannelPermission.PERMISSION_COLUMN, permission);
						
						QueryBuilder<UserGroup, Integer> qbGroup = databaseManager.getDao(UserGroup.class, Integer.class).queryBuilder();
						userGroups.addAll(qbGroup.join(qbChannelPermission).query());
					}
				}
			}
			
			QueryBuilder<UserGroupUser, Integer> qbUser = databaseManager.getDao(UserGroupUser.class, Integer.class).queryBuilder();
			qbUser.where().eq(UserGroupUser.USER_COLUMN, user.getId());
			
			QueryBuilder<UserGroup, Integer> qbGroup = databaseManager.getDao(UserGroup.class, Integer.class).queryBuilder();
			userGroups.addAll(qbGroup.join(qbUser).query());
			
			return new ArrayList<>(userGroups);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public boolean permissionGranted(User user, TextChannel channel, Plugin plugin, String subpermission) {
		return permissionGranted(user, channel, plugin.info, subpermission);
	}
	
	public boolean permissionGranted(User user, TextChannel channel, Plugin.Info pluginInfo, String subpermission) {
		return permissionGranted(user, channel, String.format("%s.%s", pluginInfo.packageName(), subpermission));
	}
	
	public boolean permissionGranted(User user, TextChannel channel, String permission) {
		for (UserGroup group : getUserGroups(user, channel)) {
			if (group.permissionGranted(permission))
				return true;
		}
		return false;
	}
	
	public boolean permissionGranted(User user, Guild guild, Plugin plugin, String subpermission) {
		return permissionGranted(user, guild, plugin.info, subpermission);
	}
	
	public boolean permissionGranted(User user, Guild guild, Plugin.Info pluginInfo, String subpermission) {
		return permissionGranted(user, guild, String.format("%s.%s", pluginInfo.packageName(), subpermission));
	}
	
	public boolean permissionGranted(User user, Guild guild, String permission) {
		for (UserGroup group : getUserGroups(user, guild)) {
			if (group.permissionGranted(permission))
				return true;
		}
		return false;
	}
	
	public boolean permissionGranted(User user, Plugin plugin, String subpermission) {
		return permissionGranted(user, plugin.info, subpermission);
	}
	
	public boolean permissionGranted(User user, Plugin.Info pluginInfo, String subpermission) {
		return permissionGranted(user, String.format("%s.%s", pluginInfo.packageName(), subpermission));
	}
	
	public boolean permissionGranted(User user, String permission) {
		for (UserGroup group : getUserGroups(user)) {
			if (group.permissionGranted(permission))
				return true;
		}
		return false;
	}
	
	@Override
	protected void onLoad() {
		DatabaseManager databaseManager = manager.app.getDatabaseManager();
		
		if (databaseManager.count(UserGroup.class) == 0) {
			String adminUserID = getConfig().getOptionalString("defaultAdminUserID");
			if (adminUserID != null) {
				UserGroup group = databaseManager.create(UserGroup.class, obj -> {
					obj.name = "admin";
				});
				databaseManager.create(UserGroupUser.class, obj -> {
					obj.user = adminUserID;
					obj.setUserGroup(group);
				});
				databaseManager.create(UserGroupPermission.class, obj -> {
					obj.permission = "*";
					obj.setUserGroup(group);
				});
			}
		}
	}
}