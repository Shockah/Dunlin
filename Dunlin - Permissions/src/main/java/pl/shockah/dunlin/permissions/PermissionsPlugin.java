package pl.shockah.dunlin.permissions;

import com.j256.ormlite.stmt.QueryBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
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
import pl.shockah.plugin.PluginInfo;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionsPlugin extends Plugin {
	@Nonnull public static final Color MISSING_PERMISSION_EMBED_COLOR = new Color(1.0f, 0.35f, 0.35f);

	public PermissionsPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info) {
		super(manager, info);
	}

	@Nonnull public List<PermissionGroup> getPermissionGroupsForUser(@Nonnull User user) {
		DatabaseManager db = manager.app.getDatabaseManager();
		return db.select(PermissionGroup.class, q -> {
			QueryBuilder<PermissionUser, Integer> qPermissionUser = db.getDao(PermissionUser.class).queryBuilder();
			qPermissionUser.where().eq(PermissionUser.USER_ID, user.getIdLong());
			q.join(qPermissionUser);
		});
	}

	@Nonnull public List<PermissionGroup> getPermissionGroupsForRole(@Nonnull Role role) {
		DatabaseManager db = manager.app.getDatabaseManager();
		return db.select(PermissionGroup.class, q -> {
			QueryBuilder<PermissionRole, Integer> qPermissionRole = db.getDao(PermissionRole.class).queryBuilder();
			qPermissionRole.where()
					.eq(PermissionRole.GUILD_ID, role.getGuild().getIdLong())
					.and().eq(PermissionRole.ROLE_ID, role.getIdLong());
			q.join(qPermissionRole);
		});
	}

	@Nonnull public List<PermissionGroup> getPermissionGroupsForMember(@Nonnull Member member) {
		List<PermissionGroup> groups = new ArrayList<>();
		groups.addAll(getPermissionGroupsForUser(member.getUser()));
		for (Role role : member.getRoles()) {
			groups.addAll(getPermissionGroupsForRole(role));
		}
		return groups;
	}

	public void checkPermission(@Nonnull User user, @Nonnull Plugin plugin, @Nonnull String permission) {
		if (!hasPermission(user, plugin, permission))
			checkPermissionThrowSecurityException(permission);
	}

	public void checkPermission(@Nonnull User user, @Nonnull String permission) {
		if (!hasPermission(user, permission))
			checkPermissionThrowSecurityException(permission);
	}

	public void checkPermission(@Nonnull Message message, @Nonnull Plugin plugin, @Nonnull String permission) {
		if (!hasPermission(message, plugin, permission))
			checkPermissionThrowSecurityException(permission);
	}

	public void checkPermission(@Nonnull Message message, @Nonnull String permission) {
		if (!hasPermission(message, permission))
			checkPermissionThrowSecurityException(permission);
	}

	public void checkPermission(@Nonnull Member member, @Nonnull Plugin plugin, @Nonnull String permission) {
		if (!hasPermission(member, plugin, permission))
			checkPermissionThrowSecurityException(permission);
	}

	public void checkPermission(@Nonnull Member member, @Nonnull String permission) {
		if (!hasPermission(member, permission))
			checkPermissionThrowSecurityException(permission);
	}

	private void checkPermissionThrowSecurityException(@Nonnull String permission) {
		throw new SecurityException(getMissingPermissionMessage(permission));
	}

	@Nonnull public String getMissingPermissionMessage(@Nonnull Plugin plugin, @Nonnull String permission) {
		return getMissingPermissionMessage(String.format("%s.%s", plugin.info.getPackageName(), permission));
	}

	@Nonnull public String getMissingPermissionMessage(@Nonnull String permission) {
		return String.format("Missing permission: `%s`.", permission);
	}

	@Nonnull public Message buildMissingPermissionMessage(@Nonnull Plugin plugin, @Nonnull String permission) {
		return buildMissingPermissionMessage(String.format("%s.%s", plugin.info.getPackageName(), permission));
	}

	@Nonnull public Message buildMissingPermissionMessage(@Nonnull String permission) {
		return new MessageBuilder().setEmbed(new EmbedBuilder()
				.setColor(MISSING_PERMISSION_EMBED_COLOR)
				.setDescription(getMissingPermissionMessage(permission))
				.build())
		.build();
	}

	public boolean hasPermission(@Nonnull User user, @Nonnull Plugin plugin, @Nonnull String permission) {
		return hasPermission(user, String.format("%s.%s", plugin.info.getPackageName(), permission));
	}

	public boolean hasPermission(@Nonnull User user, @Nonnull String permission) {
		for (PermissionGroup group : getPermissionGroupsForUser(user)) {
			if (group.hasPermission(permission))
				return true;
		}
		return false;
	}

	public boolean hasPermission(@Nonnull Message message, @Nonnull Plugin plugin, @Nonnull String permission) {
		return hasPermission(message.getGuild().getMember(message.getAuthor()), plugin, permission);
	}

	public boolean hasPermission(@Nonnull Message message, @Nonnull String permission) {
		return hasPermission(message.getGuild().getMember(message.getAuthor()), permission);
	}

	public boolean hasPermission(@Nonnull Member member, @Nonnull Plugin plugin, @Nonnull String permission) {
		return hasPermission(member, String.format("%s.%s", plugin.info.getPackageName(), permission));
	}

	public boolean hasPermission(@Nonnull Member member, @Nonnull String permission) {
		for (PermissionGroup group : getPermissionGroupsForMember(member)) {
			if (group.hasPermission(permission))
				return true;
		}
		return false;
	}
}