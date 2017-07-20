package pl.shockah.dunlin.groovyscripting;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.Presence;
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
            whitelistedClasses.addAll(Arrays.asList(
                    AccountType.class,
                    OnlineStatus.class,
                    Game.class,
                    Permission.class,
                    Guild.MFALevel.class,
                    Region.class,
                    Guild.Timeout.class,
                    Guild.VerificationLevel.class,
                    Guild.NotificationLevel.class,
                    EmbedBuilder.class
            ));

            whitelistedMethods.addAll(Arrays.asList(
                    RestAction.class.getMethod("complete"),
                    RestAction.class.getMethod("complete", boolean.class),

                    ISnowflake.class.getMethod("getId"),
                    ISnowflake.class.getMethod("getIdLong"),
                    ISnowflake.class.getMethod("getCreationTime"),

                    IMentionable.class.getMethod("getAsMention"),

                    IFakeable.class.getMethod("isFake"),

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
                    Emote.class.getMethod("getRoles"),

                    JDA.class.getMethod("getSelfUser"),
                    JDA.class.getMethod("getAccountType"),
                    JDA.class.getMethod("getPresence"),
                    JDA.class.getMethod("getShardInfo"),
                    JDA.class.getMethod("getStatus"),
                    JDA.class.getMethod("isAudioEnabled"),
                    JDA.class.getMethod("isAutoReconnect"),
                    JDA.class.getMethod("isBulkDeleteSplittingEnabled"),

                    JDA.ShardInfo.class.getMethod("getShardId"),
                    JDA.ShardInfo.class.getMethod("getShardString"),
                    JDA.ShardInfo.class.getMethod("getShardTotal"),

                    Webhook.class.getMethod("getName"),
                    Webhook.class.getMethod("getChannel"),
                    Webhook.class.getMethod("getDefaultUser"),
                    Webhook.class.getMethod("getGuild"),
                    Webhook.class.getMethod("getJDA"),
                    Webhook.class.getMethod("getOwner"),

                    Invite.class.getMethod("getJDA"),
                    Invite.class.getMethod("getGuild"),
                    Invite.class.getMethod("getChannel"),
                    Invite.class.getMethod("getCode"),
                    Invite.class.getMethod("getCreationTime"),
                    Invite.class.getMethod("getInviter"),
                    Invite.class.getMethod("getMaxAge"),
                    Invite.class.getMethod("getMaxUses"),
                    Invite.class.getMethod("getUses"),
                    Invite.class.getMethod("isExpanded"),
                    Invite.class.getMethod("isTemporary"),
                    Invite.class.getMethod("resolve", JDA.class, String.class),

                    PermissionOverride.class.getMethod("getAllowedRaw"),
                    PermissionOverride.class.getMethod("getInheritRaw"),
                    PermissionOverride.class.getMethod("getDeniedRaw"),
                    PermissionOverride.class.getMethod("getAllowed"),
                    PermissionOverride.class.getMethod("getInherit"),
                    PermissionOverride.class.getMethod("getDenied"),
                    PermissionOverride.class.getMethod("getJDA"),
                    PermissionOverride.class.getMethod("getMember"),
                    PermissionOverride.class.getMethod("getRole"),
                    PermissionOverride.class.getMethod("getChannel"),
                    PermissionOverride.class.getMethod("getGuild"),
                    PermissionOverride.class.getMethod("isMemberOverride"),
                    PermissionOverride.class.getMethod("isRoleOverride"),

                    SelfUser.class.getMethod("isVerified"),
                    SelfUser.class.getMethod("isMfaEnabled"),

                    Presence.class.getMethod("getJDA"),
                    Presence.class.getMethod("getStatus"),
                    Presence.class.getMethod("getGame"),
                    Presence.class.getMethod("isIdle")
            ));
        } catch (NoSuchMethodException e) {
            throw new UnexpectedException(e);
        }
    }
}