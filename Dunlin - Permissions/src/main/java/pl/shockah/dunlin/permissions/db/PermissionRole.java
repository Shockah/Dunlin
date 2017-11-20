package pl.shockah.dunlin.permissions.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import pl.shockah.dunlin.ShardManager;
import pl.shockah.dunlin.db.DbObject;
import pl.shockah.dunlin.db.DbObject.TableVersion;

import javax.annotation.Nonnull;

@DatabaseTable(tableName = "pl_shockah_dunlin_permissions_db_PermissionRole")
@TableVersion(1)
public class PermissionRole extends DbObject<PermissionRole> {
	@DatabaseField(columnName = GUILD_ID, canBeNull = false)
	private long guildId;
	@Nonnull public static final String GUILD_ID = "guildId";
	
	@DatabaseField(columnName = ROLE_ID, canBeNull = false)
	private long roleId;
	@Nonnull public static final String ROLE_ID = "roleId";
	
	@DatabaseField(columnName = GROUP, canBeNull = false, foreign = true)
	private PermissionGroup group;
	@Nonnull public static final String GROUP = "group";
	
	@Deprecated //ORMLite-only
	PermissionRole() {
		super();
	}
	
	public PermissionRole(@Nonnull Dao<PermissionRole, Integer> dao) {
		super(dao);
	}
	
	public long getGuildId() {
		return guildId;
	}
	
	public void setGuildId(long guildId) {
		this.guildId = guildId;
	}
	
	public long getRoleId() {
		return roleId;
	}
	
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	public PermissionGroup getGroup() {
		if (group != null)
			group.refresh();
		return group;
	}
	
	public void setGroup(@Nonnull PermissionGroup group) {
		this.group = group;
	}
	
	public Role getRole(@Nonnull ShardManager manager) {
		Guild guild = manager.getGuildById(guildId);
		if (guild == null)
			throw new IllegalArgumentException();
		return guild.getRoleById(roleId);
	}
	
	public Role getRole(@Nonnull JDA jda) {
		return jda.getGuildById(guildId).getRoleById(roleId);
	}
	
	public void setRole(@Nonnull Role role) {
		setGuildId(role.getGuild().getIdLong());
		setRoleId(role.getIdLong());
	}
}