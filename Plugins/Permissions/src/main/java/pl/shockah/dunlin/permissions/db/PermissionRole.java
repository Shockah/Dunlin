package pl.shockah.dunlin.permissions.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Role;
import pl.shockah.dunlin.InstanceManager;
import pl.shockah.dunlin.db.DbObject;
import pl.shockah.dunlin.db.DbObject.TableVersion;

@DatabaseTable(tableName = "pl_shockah_dunlin_permissions_db_PermissionRole")
@TableVersion(1)
public class PermissionRole extends DbObject<PermissionRole> {
	@DatabaseField(columnName = GUILD_ID, canBeNull = false)
	private String guildId;
	public static final String GUILD_ID = "guildId";
	
	@DatabaseField(columnName = ROLE_ID, canBeNull = false)
	private String roleId;
	public static final String ROLE_ID = "roleId";
	
	@DatabaseField(columnName = GROUP, canBeNull = false, foreign = true)
	private PermissionGroup group;
	public static final String GROUP = "group";
	
	@Deprecated //ORMLite-only
	PermissionRole() {
		super();
	}
	
	public PermissionRole(Dao<PermissionRole, Integer> dao) {
		super(dao);
	}
	
	public String getGuildId() {
		return guildId;
	}
	
	public void setGuildId(String guildId) {
		this.guildId = guildId;
	}
	
	public String getRoleId() {
		return roleId;
	}
	
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	public PermissionGroup getGroup() {
		if (group != null)
			group.refresh();
		return group;
	}
	
	public void setGroup(PermissionGroup group) {
		this.group = group;
	}
	
	public Role getRole(InstanceManager manager) {
		return manager.getGuildById(guildId).getRoleById(roleId);
	}
	
	public Role getRole(JDA jda) {
		return jda.getGuildById(guildId).getRoleById(roleId);
	}
	
	public void setRole(Role role) {
		setGuildId(role.getGuild().getId());
		setRoleId(role.getId());
	}
}