package io.shockah.dunlin.permissions.db;

import java.sql.SQLException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.dunlin.db.DbObject;
import net.dv8tion.jda.core.entities.User;
import pl.shockah.util.UnexpectedException;

@DatabaseTable(tableName = "io_shockah_dunlin_permissions_usergroupusers")
public class UserGroupUser extends DbObject<UserGroupUser> {
	public static final String USER_COLUMN = "user";
	public static final String USERGROUP_COLUMN = "usergroup_id";
	
	@DatabaseField(canBeNull = false, columnName = USER_COLUMN)
	public String user;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = USERGROUP_COLUMN)
	private UserGroup userGroup;
	
	@Deprecated //ORMLite-only
	protected UserGroupUser() {
		super();
	}
	
	@Deprecated //ORMLite-only
	public UserGroupUser(Dao<UserGroupUser, Integer> dao) {
		super(dao);
	}
	
	public UserGroupUser(Dao<UserGroupUser, Integer> dao, UserGroup userGroup) {
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
	
	public boolean matches(User user) {
		return user.getId().equals(this.user);
	}
}