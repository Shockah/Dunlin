package pl.shockah.dunlin.permissions.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import pl.shockah.dunlin.ShardManager;
import pl.shockah.dunlin.db.DbObject;
import pl.shockah.dunlin.db.DbObject.TableVersion;

@DatabaseTable(tableName = "pl_shockah_dunlin_permissions_db_PermissionUser")
@TableVersion(1)
public class PermissionUser extends DbObject<PermissionUser> {
	@DatabaseField(columnName = USER_ID)
	private long userId;
	public static final String USER_ID = "userId";
	
	@DatabaseField(columnName = GROUP, canBeNull = false, foreign = true)
	private PermissionGroup group;
	public static final String GROUP = "group";
	
	@Deprecated //ORMLite-only
	PermissionUser() {
		super();
	}
	
	public PermissionUser(Dao<PermissionUser, Integer> dao) {
		super(dao);
	}
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public PermissionGroup getGroup() {
		if (group != null)
			group.refresh();
		return group;
	}
	
	public void setGroup(PermissionGroup group) {
		this.group = group;
	}
	
	public User getUser(ShardManager manager) {
		return manager.getUserById(userId);
	}
	
	public User getUser(JDA jda) {
		return jda.getUserById(userId);
	}
	
	public void setUser(User user) {
		setUserId(user.getIdLong());
	}
}