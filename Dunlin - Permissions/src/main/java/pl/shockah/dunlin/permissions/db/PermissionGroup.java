package pl.shockah.dunlin.permissions.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import pl.shockah.dunlin.db.DbObject;
import pl.shockah.dunlin.db.DbObject.TableVersion;
import pl.shockah.dunlin.db.ForeignCollectionWrapper;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.json.JSONList;

@DatabaseTable(tableName = "pl_shockah_dunlin_permissions_db_PermissionGroup")
@TableVersion(1)
public class PermissionGroup extends DbObject<PermissionGroup> {
	@DatabaseField(columnName = NAME, canBeNull = false)
	private String name;
	public static final String NAME = "name";
	
	@DatabaseField(columnName = PERMISSIONS, canBeNull = false)
	private JSONList<String> permissions = new JSONList<>();
	public static final String PERMISSIONS = "permissions";
	
	@ForeignCollectionField(foreignFieldName = PermissionRole.GROUP)
	private ForeignCollection<PermissionRole> roles;
	
	@ForeignCollectionField(foreignFieldName = PermissionUser.GROUP)
	private ForeignCollection<PermissionUser> users;
	
	@Deprecated //ORMLite-only
	PermissionGroup() {
		super();
	}
	
	public PermissionGroup(Dao<PermissionGroup, Integer> dao) {
		super(dao);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public JSONList<String> getPermissions() {
		return permissions;
	}
	
	public void setPermissions(JSONList<String> permissions) {
		this.permissions = permissions;
	}
	
	public ForeignCollectionWrapper<PermissionRole> getRoles() {
		return new ForeignCollectionWrapper<>(roles);
	}
	
	public ForeignCollectionWrapper<PermissionUser> getUsers() {
		return new ForeignCollectionWrapper<>(users);
	}

	public boolean hasPermission(Plugin plugin, String permission) {
		return hasPermission(String.format("%s.%s", plugin.info.packageName(), permission));
	}

	public boolean hasPermission(String permission) {
		for (String myPermission : permissions) {
			String[] spl = myPermission.split("\\.");
			String[] splArg = permission.split("\\.");

			if (spl.length > splArg.length)
				return false;

			for (int i = 0; i < spl.length; i++) {
				String s = spl[i];
				String arg = splArg[i];

				if (s.equals("*"))
					return true;
				if (!s.equals(arg))
					return false;
			}
			return true;
		}
		return false;
	}
}