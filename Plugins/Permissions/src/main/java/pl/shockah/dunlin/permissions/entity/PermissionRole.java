package pl.shockah.dunlin.permissions.entity;

import javax.persistence.Entity;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Role;

@Entity
public class PermissionRole {
	private String guildId;
	
	private String roleId;
	
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
	
	public Role getRole(JDA jda) {
		return jda.getGuildById(guildId).getRoleById(roleId);
	}
	
	public void setRole(Role role) {
		setGuildId(role.getGuild().getId());
		setRoleId(role.getId());
	}
}