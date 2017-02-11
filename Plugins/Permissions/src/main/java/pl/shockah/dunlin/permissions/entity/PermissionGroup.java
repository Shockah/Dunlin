package pl.shockah.dunlin.permissions.entity;

import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import pl.shockah.dunlin.db.DbObject;
import pl.shockah.dunlin.db.DbObject.TableVersion;
import pl.shockah.dunlin.db.ForeignCollectionWrapper;
import pl.shockah.dunlin.db.JSONListPersister;

@DatabaseTable(tableName = "pl_shockah_dunlin_permissions_entity_PermissionGroup")
@TableVersion(1)
public class PermissionGroup extends DbObject<PermissionGroup> {
	@DatabaseField(columnName = NAME, canBeNull = false)
	private String name;
	public static final String NAME = "name";
	
	@DatabaseField(columnName = PERMISSIONS, canBeNull = false, persisterClass = JSONListPersister.class)
	private List<String> permissions = new ArrayList<>();
	public static final String PERMISSIONS = "permissions";
	
	@ForeignCollectionField(foreignFieldName = PermissionRole.GROUP)
	private ForeignCollection<PermissionRole> roles;
	
	@Deprecated
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
	
	public List<String> getPermissions() {
		return permissions;
	}
	
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
	
	public ForeignCollectionWrapper<PermissionRole> getRoles() {
		return new ForeignCollectionWrapper<>(roles);
	}
}