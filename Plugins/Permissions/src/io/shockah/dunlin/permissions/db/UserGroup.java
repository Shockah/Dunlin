package io.shockah.dunlin.permissions.db;

import java.io.IOException;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.dunlin.UnexpectedException;
import io.shockah.dunlin.db.DbObject;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

@DatabaseTable(tableName = "io_shockah_dunlin_permissions_usergroups")
public class UserGroup extends DbObject<UserGroup> {
	@DatabaseField(canBeNull = false)
	public String name;
	
	@ForeignCollectionField(foreignFieldName = "userGroup")
	private ForeignCollection<UserGroupUser> users;
	
	@ForeignCollectionField(foreignFieldName = "userGroup")
	private ForeignCollection<UserGroupRole> roles;
	
	@ForeignCollectionField(foreignFieldName = "userGroup")
	private ForeignCollection<UserGroupPermission> permissions;
	
	@Deprecated //ORMLite-only
	protected UserGroup() {
		super();
	}
	
	public UserGroup(Dao<UserGroup, Integer> dao) {
		super(dao);
	}
	
	public ForeignCollection<UserGroupUser> getUsers() {
		return users;
	}
	
	public ForeignCollection<UserGroupRole> getRoles() {
		return roles;
	}
	
	public ForeignCollection<UserGroupPermission> getPermissions() {
		return permissions;
	}
	
	public boolean matches(User user, Guild guild) {
		try (CloseableWrappedIterable<UserGroupUser> cUsers = users.getWrappedIterable()) {
			for (UserGroupUser dbUser : cUsers) {
				if (dbUser.matches(user))
					return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (guild != null) {
			try (CloseableWrappedIterable<UserGroupRole> cRoles = roles.getWrappedIterable()) {
				for (UserGroupRole dbRole : cRoles) {
					if (dbRole.matches(user, guild))
						return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean permissionGranted(String actionPermission) {
		try (CloseableWrappedIterable<UserGroupPermission> cPermissions = permissions.getWrappedIterable()) {
			for (UserGroupPermission permission : cPermissions) {
				if (permission.permissionGranted(actionPermission))
					return true;
			}
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
		return false;
	}
}