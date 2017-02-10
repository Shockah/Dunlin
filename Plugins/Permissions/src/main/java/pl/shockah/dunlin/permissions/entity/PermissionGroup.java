package pl.shockah.dunlin.permissions.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import io.ebean.annotation.DbArray;

@Entity
public class PermissionGroup {
	private String name;
	
	@DbArray
	private List<String> permissions = new ArrayList<>();
	
	@OneToMany(mappedBy = "group", cascade = CascadeType.PERSIST)
	private List<PermissionRole> roles = new ArrayList<>();
	
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
	
	public List<PermissionRole> getRoles() {
		return roles;
	}
	
	public void setRoles(List<PermissionRole> roles) {
		this.roles = roles;
	}
}