package io.shockah.dunlin.permissions.db;

import java.sql.SQLException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.dunlin.UnexpectedException;
import io.shockah.dunlin.db.DbObject;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;

@DatabaseTable(tableName = "io_shockah_dunlin_permissions_usergrouproles")
public class UserGroupRole extends DbObject<UserGroupRole> {
	public static final String ROLE_COLUMN = "role";
	public static final String USERGROUP_COLUMN = "usergroup_id";
	
	@DatabaseField(canBeNull = false, columnName = ROLE_COLUMN)
	public String role;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = USERGROUP_COLUMN)
	private UserGroup userGroup;
	
	@Deprecated //ORMLite-only
	protected UserGroupRole() {
		super();
	}
	
	@Deprecated //ORMLite-only
	public UserGroupRole(Dao<UserGroupRole, Integer> dao) {
		super(dao);
	}
	
	public UserGroupRole(Dao<UserGroupRole, Integer> dao, UserGroup userGroup) {
		super(dao);
		this.userGroup = userGroup;
	}
	
	public UserGroup getUserGroup() throws SQLException {
		try {
			if (userGroup != null)
				userGroup.refresh();
			return userGroup;
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}
	
	public boolean matches(Role role) {
		return role.getId().equals(this.role);
	}
	
	public boolean matches(User user, Guild guild) {
		for (Role role : guild.getRolesForUser(user)) {
			if (matches(role))
				return true;
		}
		return false;
	}
}