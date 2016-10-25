package io.shockah.dunlin.groovy;

import java.net.URLDecoder;
import java.net.URLEncoder;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandResult;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.entities.Channel;
import net.dv8tion.jda.entities.Emote;
import net.dv8tion.jda.entities.Game;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.MessageEmbed;
import net.dv8tion.jda.entities.MessageType;
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
		addWhitelistedMethods(MessageChannel.class,
				"getId", "getJDA", "getPinnedMessages", "getMessageById", "getHistory"
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
		addWhitelistedMethods(Message.class,
				"getId", "getMentionedUsers", "isMentioned", "getMentionedChannels", "getMentionedRoles", "mentionsEveryone",
				"getTime", "isEdited", "getEditedTimestamp", "getAuthor", "getContent", "getRawContent", "getStrippedContent",
				"isPrivate", "getChannelId", "getChannel", "getAttachments", "getEmbeds", "getEmotes", "isTTS", "getJDA", "isPinned",
				"getType"
		);
		addWhitelistedMethods(Message.Attachment.class,
				"getId", "getUrl", "getProxyUrl", "getFileName", "getSize", "getHeight", "getWidth", "isImage"
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
		addWhitelistedMethods(MessageHistory.class,
				"retrieve", "getRecent"
		);
		
		addWhitelistedPackages(
				"io.shockah.dunlin.commands"
		);
		
		addWhitelistedClasses(
				CommandResult.class, URLEncoder.class, URLDecoder.class,
				Role.class, Game.class, Emote.class, OnlineStatus.class, MessageType.class,
				MessageEmbed.class, MessageEmbed.Thumbnail.class, MessageEmbed.Provider.class, MessageEmbed.VideoInfo.class
		);
	}
}