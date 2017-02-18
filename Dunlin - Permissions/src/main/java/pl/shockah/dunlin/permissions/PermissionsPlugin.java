package pl.shockah.dunlin.permissions;

import com.j256.ormlite.stmt.QueryBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import pl.shockah.dunlin.db.DatabaseManager;
import pl.shockah.dunlin.permissions.db.PermissionGroup;
import pl.shockah.dunlin.permissions.db.PermissionRole;
import pl.shockah.dunlin.permissions.db.PermissionUser;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class PermissionsPlugin extends Plugin {
	public PermissionsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}

	public List<PermissionGroup> getPermissionGroupsForUser(User user) {
		DatabaseManager db = manager.app.getDatabaseManager();
		return db.select(PermissionGroup.class, q -> {
			QueryBuilder<PermissionUser, Integer> qPermissionUser = db.getDao(PermissionUser.class).queryBuilder();
			qPermissionUser.where().eq(PermissionUser.USER_ID, user.getId());
			q.join(qPermissionUser);
		});
	}

	public List<PermissionGroup> getPermissionGroupsForRole(Role role) {
		DatabaseManager db = manager.app.getDatabaseManager();
		return db.select(PermissionGroup.class, q -> {
			QueryBuilder<PermissionRole, Integer> qPermissionRole = db.getDao(PermissionRole.class).queryBuilder();
			qPermissionRole.where()
					.eq(PermissionRole.GUILD_ID, role.getGuild().getId())
					.and().eq(PermissionRole.ROLE_ID, role.getId());
			q.join(qPermissionRole);
		});
	}

	public List<PermissionGroup> getPermissionGroupsForMember(Member member) {
		List<PermissionGroup> groups = new ArrayList<>();
		groups.addAll(getPermissionGroupsForUser(member.getUser()));
		for (Role role : member.getRoles()) {
			groups.addAll(getPermissionGroupsForRole(role));
		}
		return groups;
	}

	public void checkPermission(User user, Plugin plugin, String permission) {
		if (!hasPermission(user, plugin, permission))
			checkPermissionThrowSecurityException(permission);
	}

	public void checkPermission(User user, String permission) {
		if (!hasPermission(user, permission))
			checkPermissionThrowSecurityException(permission);
	}

	public void checkPermission(Message message, Plugin plugin, String permission) {
		if (!hasPermission(message, plugin, permission))
			checkPermissionThrowSecurityException(permission);
	}

	public void checkPermission(Message message, String permission) {
		if (!hasPermission(message, permission))
			checkPermissionThrowSecurityException(permission);
	}

	public void checkPermission(Member member, Plugin plugin, String permission) {
		if (!hasPermission(member, plugin, permission))
			checkPermissionThrowSecurityException(permission);
	}

	public void checkPermission(Member member, String permission) {
		if (!hasPermission(member, permission))
			checkPermissionThrowSecurityException(permission);
	}

	private void checkPermissionThrowSecurityException(String permission) {
		throw new SecurityException(getMissingPermissionMessage(permission));
	}

	public String getMissingPermissionMessage(Plugin plugin, String permission) {
		return getMissingPermissionMessage(String.format("%s.%s", plugin.info.packageName(), permission));
	}

	public String getMissingPermissionMessage(String permission) {
		return String.format("Missing permission: `%s`.", permission);
	}

	public boolean hasPermission(User user, Plugin plugin, String permission) {
		return hasPermission(user, String.format("%s.%s", plugin.info.packageName(), permission));
	}

	public boolean hasPermission(User user, String permission) {
		for (PermissionGroup group : getPermissionGroupsForUser(user)) {
			if (group.hasPermission(permission))
				return true;
		}
		return false;
	}

	public boolean hasPermission(Message message, Plugin plugin, String permission) {
		return hasPermission(message.getGuild().getMember(message.getAuthor()), plugin, permission);
	}

	public boolean hasPermission(Message message, String permission) {
		return hasPermission(message.getGuild().getMember(message.getAuthor()), permission);
	}

	public boolean hasPermission(Member member, Plugin plugin, String permission) {
		return hasPermission(member, String.format("%s.%s", plugin.info.packageName(), permission));
	}

	public boolean hasPermission(Member member, String permission) {
		for (PermissionGroup group : getPermissionGroupsForMember(member)) {
			if (group.hasPermission(permission))
				return true;
		}
		return false;
	}
}