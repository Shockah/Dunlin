package pl.shockah.dunlin.permissions.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import pl.shockah.dunlin.db.DbObject;
import pl.shockah.dunlin.db.DbObject.TableVersion;
import pl.shockah.dunlin.db.ForeignCollectionWrapper;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.json.JSONList;

import javax.annotation.Nonnull;

@DatabaseTable(tableName = "pl_shockah_dunlin_permissions_db_PermissionGroup")
@TableVersion(1)
public class PermissionGroup extends DbObject<PermissionGroup> {
	@DatabaseField(columnName = NAME, canBeNull = false)
	private String name;
	@Nonnull public static final String NAME = "name";
	
	@DatabaseField(columnName = PERMISSIONS, canBeNull = false)
	@Nonnull private JSONList<String> permissions = new JSONList<>();
	@Nonnull public static final String PERMISSIONS = "permissions";
	
	@ForeignCollectionField(foreignFieldName = PermissionRole.GROUP)
	private ForeignCollection<PermissionRole> roles;
	
	@ForeignCollectionField(foreignFieldName = PermissionUser.GROUP)
	private ForeignCollection<PermissionUser> users;
	
	@Deprecated //ORMLite-only
	PermissionGroup() {
		super();
	}
	
	public PermissionGroup(@Nonnull Dao<PermissionGroup, Integer> dao) {
		super(dao);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(@Nonnull String name) {
		this.name = name;
	}
	
	@Nonnull public JSONList<String> getPermissions() {
		return permissions;
	}
	
	public void setPermissions(@Nonnull JSONList<String> permissions) {
		this.permissions = permissions;
	}
	
	@Nonnull public ForeignCollectionWrapper<PermissionRole> getRoles() {
		return new ForeignCollectionWrapper<>(roles);
	}
	
	@Nonnull public ForeignCollectionWrapper<PermissionUser> getUsers() {
		return new ForeignCollectionWrapper<>(users);
	}

	public boolean hasPermission(@Nonnull Plugin plugin, @Nonnull String permission) {
		return hasPermission(String.format("%s.%s", plugin.info.getPackageName(), permission));
	}

	public boolean hasPermission(@Nonnull String permission) {
		L: for (String myPermission : permissions) {
			String[] spl = myPermission.split("\\.");
			String[] splArg = permission.split("\\.");

			if (spl.length > splArg.length)
				continue;

			for (int i = 0; i < spl.length; i++) {
				String s = spl[i];
				String arg = splArg[i];

				if (s.equals("*"))
					return true;
				if (!s.equals(arg))
					continue L;
			}
			return true;
		}
		return false;
	}
}