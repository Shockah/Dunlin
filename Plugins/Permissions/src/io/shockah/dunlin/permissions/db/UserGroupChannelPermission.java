package io.shockah.dunlin.permissions.db;

import java.sql.SQLException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.dunlin.UnexpectedException;
import io.shockah.dunlin.db.DbObject;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

@DatabaseTable(tableName = "io_shockah_dunlin_permissions_usergroupchannelpermissions")
public class UserGroupChannelPermission extends DbObject<UserGroupChannelPermission> {
	public static final String PERMISSION_COLUMN = "permission";
	public static final String USERGROUP_COLUMN = "usergroup_id";
	
	@DatabaseField(canBeNull = false, columnName = PERMISSION_COLUMN)
	public Permission permission;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = USERGROUP_COLUMN)
	private UserGroup userGroup;
	
	@Deprecated //ORMLite-only
	protected UserGroupChannelPermission() {
		super();
	}
	
	@Deprecated //ORMLite-only
	public UserGroupChannelPermission(Dao<UserGroupChannelPermission, Integer> dao) {
		super(dao);
	}
	
	public UserGroupChannelPermission(Dao<UserGroupChannelPermission, Integer> dao, UserGroup userGroup) {
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
	
	public boolean matches(User user, TextChannel channel) {
		return channel.checkPermission(user, permission);
	}
}