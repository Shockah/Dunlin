package io.shockah.dunlin.groovy;

import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandResult;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.entities.Channel;
import net.dv8tion.jda.entities.Emote;
import net.dv8tion.jda.entities.Game;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.managers.AudioManager;
import net.dv8tion.jda.managers.ChannelManager;
import net.dv8tion.jda.managers.GuildManager;
import net.dv8tion.jda.managers.RoleManager;

public class UserGroovySandboxImpl extends GroovySandboxImpl {
	public UserGroovySandboxImpl() {
		super();
		
		addBlacklistedMethods(CommandCall.class,
				"respond"
		);
		
		addWhitelistedMethods(JDA.class,
				"getUsers", "getUserById", "getGuildById", "getTextChannelById", "getVoiceChannelById", "getSelfInfo", "getEmoteById", "getAudioManager"
		);
		addWhitelistedMethods(User.class,
				"getId", "getUsername", "getDiscriminator", "getAsMention", "getAvatarId", "getAvatarUrl",
				"getDefaultAvatarId", "getDefaultAvatarUrl", "getCurrentGame", "getOnlineStatus", "isBot", "getJDA"
		);
		addWhitelistedMethods(TextChannel.class,
				"getAsMention"
		);
		addWhitelistedMethods(VoiceChannel.class,
				"getUserLimit", "getBitrate"
		);
		addWhitelistedMethods(Channel.class,
				"getId", "getName", "getTopic", "getGuild", "getUsers", "getPosition", "getPositionRaw", "checkPermission", "getManager", "getJDA"
		);
		addWhitelistedMethods(Guild.class,
				"getId", "getName", "getIconId", "getIconUrl", "getAfkChannelId", "getOwnerId", "getOwner",
				"getAfkTimeout", "getRegion", "getEmotes", "getUsers", "isMember", "getTextChannels", "getVoiceChannels",
				"getRoles", "getRoleById", "getRolesForUser", "getColorDeterminantRoleForUser", "getUsersWithRole",
				"getPublicRole", "getPublicChannel", "getJoinDateForUser", "getManager", "getAudioManager", "getJDA"
		);
		addWhitelistedMethods(GuildManager.class,
				"getGuild", "getBans"
		);
		addWhitelistedMethods(AudioManager.class,
				"getConnectedChannel", "isConnected", "getConnectTimeout"
		);
		addWhitelistedMethods(RoleManager.class,
				"getRole"
		);
		addWhitelistedMethods(ChannelManager.class,
				"getChannel"
		);
		
		addWhitelistedPackages(
				"io.shockah.dunlin.commands"
		);
		
		addWhitelistedClasses(
				CommandResult.class, Role.class, Game.class, Emote.class, OnlineStatus.class
		);
	}
}