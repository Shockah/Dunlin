package io.shockah.dunlin.groovy;

import java.awt.Color;
import java.net.URLDecoder;
import java.net.URLEncoder;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandResult;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.Region;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.EmbedType;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.IMentionable;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageType;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.VoiceState;
import net.dv8tion.jda.core.managers.Presence;
import net.dv8tion.jda.core.requests.RestAction;

public class UserGroovySandboxImpl extends GroovySandboxImpl {
	public UserGroovySandboxImpl() {
		super();
		
		addWhitelistedClasses(
			CommandResult.class, URLEncoder.class, URLDecoder.class,
			Color.class
		);
		
		addWhitelistedPackages(
			"io.shockah.dunlin.commands"
		);
		addBlacklistedMethods(CommandCall.class,
			"respond"
		);
		
		addWhitelistedClasses(
			OnlineStatus.class, Game.class, Game.GameType.class, Guild.VerificationLevel.class, Guild.NotificationLevel.class,
			Guild.MFALevel.class, Permission.class, Guild.Timeout.class, Region.class, ChannelType.class, MessageType.class,
			MessageEmbed.class, EmbedType.class, MessageEmbed.Thumbnail.class, MessageEmbed.Provider.class, MessageEmbed.AuthorInfo.class,
			MessageEmbed.VideoInfo.class, MessageEmbed.Footer.class, MessageEmbed.ImageInfo.class, MessageEmbed.Field.class
		);
		addWhitelistedMethods(RestAction.class,
			"block"
		);
		addWhitelistedMethods(JDA.class,
			"getUsers", "getUserById", "getUsersByName",
			/*"getGuilds", */"getGuildById", "getGuildsByName",
			//"getTextChannels", "getTextChannelById", "getTextChannelsByName",
			//"getVoiceChannels", "getVoiceChannelById", "getVoiceChannelByName",
			//"getPrivateChannels", "getPrivateChannelById",
			//"getEmotes", "getEmotesByName", "getEmoteById",
			"getSelfUser", "getPresence"
		);
		addWhitelistedMethods(Presence.class,
			"getJDA", "getStatus", "getGame", "isIdle"
		);
		addWhitelistedMethods(User.class,
			"getJDA", "getName", "getDiscriminator", "isBot", "getAvatarId", "getAvatarUrl",
			"getDefaultAvatarId", "getDefaultAvatarUrl", "getEffectiveAvatarUrl"
		);
		addWhitelistedMethods(IMentionable.class,
			"getAsMention"
		);
		addWhitelistedMethods(Guild.class,
			"getJDA", "getName", "getIconId",
			"getIconUrl", "getSplashId", "getSplashUrl",
			"getAfkChannel", "getOwner", "getAfkTimeout", "getRegion",
			"isMember", "getSelfMember", "getMember", "getMemberById",
			"getMembers", "getMembersByName", "getMembersByNickname", "getMembersByEffectiveName",
			"getMembersWithRoles", "getMembersWithRoles",
			"getTextChannelById", "getTextChannels", "getTextChannelsByName", "getPublicChannel",
			"getVoiceChannelById", "getVoiceChannels", "getVoiceChannelsByName",
			"getRoleById", "getRoles", "getRolesByName", "getPublicRole",
			"getEmoteById", "getEmotes", "getEmotesByName",
			"getVerificationLevel", "getDefaultNotificationLevel", "getRequiredMFALevel"
		);
		addWhitelistedMethods(Emote.class,
			"getJDA", "getGuild", "getRoles", "getName", "isManaged", "getImageUrl", "getAsMention", "canInteract"
		);
		addWhitelistedMethods(Role.class,
			"getJDA", "getPosition", "getName", "isManaged", "isHoisted", "isMentionable",
			"getPermissions", "getColor", "hasPermission", "getGuild"
		);
		addWhitelistedMethods(Member.class,
			"getJDA", "getUser", "getGuild", "getJoinDate", "getVoiceState", "getGame", "getOnlineStatus",
			"getNickname", "getEffectiveName", "getRoles", "getColor", "getPermissions", "hasPermission", "isOwner"
		);
		addWhitelistedMethods(GuildVoiceState.class,
			"isMuted", "isDeafened", "isGuildMuted", "isGuildDeafened", "isSuppressed", "getChannel", "getGuild", "getMember", "inVoiceChannel"
		);
		addWhitelistedMethods(VoiceState.class,
			"getJDA", "isSelfMuted", "isSelfDeafened"
		);
		addWhitelistedMethods(VoiceChannel.class,
			"getJDA", "getUserLimit", "getBitrate"
		);
		addWhitelistedMethods(Channel.class,
			"getJDA", "getName", "getGuild", "getMembers", "getPosition"
		);
		addWhitelistedMethods(TextChannel.class,
			"getTopic", "canTalk"
		);
		addWhitelistedMethods(MessageChannel.class,
			"getJDA", "getName", "getType", "getMessageById", "getPinnedMessages"
		);
		addWhitelistedMethods(Message.class,
			"getJDA", "getMentionedUsers", "isMentioned", "getMentionedChannels", "getMentionedRoles", "isTTS", "isPinned", "getType",
			"mentionsEveryone", "isEdited", "getEditedTime", "getAuthor", "getContent", "getRawContent", "getStrippedContent",
			"getChannel", "getTextChannel", "getGuild", "getAttachments", "getEmbeds", "getEmotes", "getReactions"
		);
		addWhitelistedMethods(Message.Attachment.class,
			"getId", "getUrl", "getProxyUrl", "getFileName", "getSize", "getHeight", "getWidth", "isImage"
		);
	}
}