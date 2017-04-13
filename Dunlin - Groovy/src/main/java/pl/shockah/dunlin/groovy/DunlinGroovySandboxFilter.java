package pl.shockah.dunlin.groovy;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RestAction;
import pl.shockah.util.UnexpectedException;

import java.util.Arrays;
import java.util.Collection;

public class DunlinGroovySandboxFilter extends BasicGroovySandboxFilter {
    public DunlinGroovySandboxFilter() {
        super();
        addJDAWhitelist();
    }

    private void addJDAWhitelist() {
        try {
            whitelistedMethods.addAll(Arrays.asList(
                    RestAction.class.getMethod("complete"),
                    RestAction.class.getMethod("complete", boolean.class),

                    ISnowflake.class.getMethod("getId"),
                    ISnowflake.class.getMethod("getCreationTime"),

                    IMentionable.class.getMethod("getAsMention"),
                    IPermissionHolder.class.getMethod("getGuild"),
                    IPermissionHolder.class.getMethod("getPermissions"),
                    IPermissionHolder.class.getMethod("hasPermission", Channel.class, Collection.class),
                    IPermissionHolder.class.getMethod("hasPermission", Channel.class, Permission[].class),
                    IPermissionHolder.class.getMethod("hasPermission", Collection.class),
                    IPermissionHolder.class.getMethod("hasPermission", Permission[].class),

                    Guild.class.getMethod("getName"),
                    Guild.class.getMethod("getEmotesByName", String.class, boolean.class),
                    Guild.class.getMethod("getEmotes"),
                    Guild.class.getMethod("getEmoteById", String.class),
                    Guild.class.getMethod("getMembersByName", String.class, boolean.class),
                    Guild.class.getMethod("getMember", User.class),
                    Guild.class.getMethod("getMembers"),
                    Guild.class.getMethod("getMemberById", String.class),
                    Guild.class.getMethod("getMembersByEffectiveName", String.class, boolean.class),
                    Guild.class.getMethod("getMembersWithRoles", Collection.class),
                    Guild.class.getMethod("getMembersWithRoles", Role[].class),
                    Guild.class.getMethod("getRolesByName", String.class, boolean.class),
                    Guild.class.getMethod("getRoles"),
                    Guild.class.getMethod("getRoleById", String.class),
                    Guild.class.getMethod("getTextChannelsByName", String.class, boolean.class),
                    Guild.class.getMethod("getTextChannels"),
                    Guild.class.getMethod("getTextChannelById", String.class),
                    Guild.class.getMethod("getVoiceChannelsByName", String.class, boolean.class),
                    Guild.class.getMethod("getVoiceChannels"),
                    Guild.class.getMethod("getVoiceChannelById", String.class),
                    Guild.class.getMethod("getVoiceStates"),
                    Guild.class.getMethod("getAfkChannel"),
                    Guild.class.getMethod("getAfkTimeout"),
                    Guild.class.getMethod("getDefaultNotificationLevel"),
                    Guild.class.getMethod("getIconId"),
                    Guild.class.getMethod("getIconUrl"),
                    Guild.class.getMethod("getJDA"),
                    Guild.class.getMethod("getInvites"),
                    Guild.class.getMethod("getOwner"),
                    Guild.class.getMethod("getPublicChannel"),
                    Guild.class.getMethod("getPublicRole"),
                    Guild.class.getMethod("getRegion"),
                    Guild.class.getMethod("getRequiredMFALevel"),
                    Guild.class.getMethod("getSelfMember"),
                    Guild.class.getMethod("getSplashId"),
                    Guild.class.getMethod("getSplashUrl"),
                    Guild.class.getMethod("getVerificationLevel"),
                    Guild.class.getMethod("getWebhooks"),
                    Guild.class.getMethod("checkVerification"),
                    Guild.class.getMethod("isAvailable"),
                    Guild.class.getMethod("isMember", User.class),

                    Member.class.getMethod("getEffectiveName"),
                    Member.class.getMethod("getColor"),
                    Member.class.getMethod("getGame"),
                    Member.class.getMethod("getGuild"),
                    Member.class.getMethod("getJDA"),
                    Member.class.getMethod("getJoinDate"),
                    Member.class.getMethod("getNickname"),
                    Member.class.getMethod("getOnlineStatus"),
                    Member.class.getMethod("getPermissions", Channel.class),
                    Member.class.getMethod("getRoles"),
                    Member.class.getMethod("getUser"),
                    Member.class.getMethod("getVoiceState"),
                    Member.class.getMethod("canInteract", Emote.class),
                    Member.class.getMethod("canInteract", Member.class),
                    Member.class.getMethod("canInteract", Role.class),
                    Member.class.getMethod("isOwner"),

                    User.class.getMethod("getName"),
                    User.class.getMethod("getJDA"),
                    User.class.getMethod("getAvatarId"),
                    User.class.getMethod("getAvatarUrl"),
                    User.class.getMethod("getDefaultAvatarId"),
                    User.class.getMethod("getDefaultAvatarUrl"),
                    User.class.getMethod("getEffectiveAvatarUrl"),
                    User.class.getMethod("getDiscriminator"),
                    User.class.getMethod("getMutualGuilds"),
                    User.class.getMethod("isBot"),

                    TextChannel.class.getMethod("canTalk"),
                    TextChannel.class.getMethod("canTalk", Member.class),
                    TextChannel.class.getMethod("getTopic"),
                    TextChannel.class.getMethod("getWebhooks"),

                    Channel.class.getMethod("getName"),
                    Channel.class.getMethod("getGuild"),
                    Channel.class.getMethod("getInvites"),
                    Channel.class.getMethod("getJDA"),
                    Channel.class.getMethod("getMembers"),
                    Channel.class.getMethod("getPermissionOverride", Member.class),
                    Channel.class.getMethod("getPermissionOverride", Role.class),
                    Channel.class.getMethod("getMemberPermissionOverrides"),
                    Channel.class.getMethod("getPermissionOverrides"),
                    Channel.class.getMethod("getPosition"),
                    Channel.class.getMethod("getPositionRaw"),
                    Channel.class.getMethod("getRolePermissionOverrides"),

                    MessageChannel.class.getMethod("getName"),
                    MessageChannel.class.getMethod("getJDA"),
                    MessageChannel.class.getMethod("getPinnedMessages"),
                    MessageChannel.class.getMethod("getType"),
                    MessageChannel.class.getMethod("getMessageById", String.class),

                    VoiceChannel.class.getMethod("getBitrate"),
                    VoiceChannel.class.getMethod("getUserLimit"),

                    VoiceState.class.getMethod("getJDA"),
                    VoiceState.class.getMethod("getAudioChannel"),
                    VoiceState.class.getMethod("getSessionId"),
                    VoiceState.class.getMethod("isSelfDeafened"),
                    VoiceState.class.getMethod("isSelfMuted"),

                    Role.class.getMethod("getJDA"),
                    Role.class.getMethod("getName"),
                    Role.class.getMethod("canInteract", Role.class),
                    Role.class.getMethod("getColor"),
                    Role.class.getMethod("getGuild"),
                    Role.class.getMethod("getPermissionsRaw"),
                    Role.class.getMethod("getPosition"),
                    Role.class.getMethod("getPositionRaw"),
                    Role.class.getMethod("isHoisted"),
                    Role.class.getMethod("isManaged"),
                    Role.class.getMethod("isMentionable"),

                    Emote.class.getMethod("isManaged"),
                    Emote.class.getMethod("getGuild"),
                    Emote.class.getMethod("canInteract", Member.class),
                    Emote.class.getMethod("canInteract", User.class, MessageChannel.class),
                    Emote.class.getMethod("getName"),
                    Emote.class.getMethod("getAsMention"),
                    Emote.class.getMethod("getImageUrl"),
                    Emote.class.getMethod("getRoles")
            ));
        } catch (NoSuchMethodException e) {
            throw new UnexpectedException(e);
        }
    }
}