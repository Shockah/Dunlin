package pl.shockah.dunlin.groovy;

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
                    //Guild.class.getMethod("getManager"),
                    //Guild.class.getMethod("getManagerUpdatable")
                    //Guild.class.getMethod("getController"),
                    //Guild.class.getMethod("getAudioManager"),

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

                    User.class.getMethod("getName"),
                    User.class.getMethod("getJDA"),
                    User.class.getMethod("getAvatarId"),
                    User.class.getMethod("getAvatarUrl"),
                    User.class.getMethod("getDefaultAvatarId"),
                    User.class.getMethod("getDefaultAvatarUrl"),
                    User.class.getMethod("getEffectiveAvatarUrl"),
                    User.class.getMethod("getDiscriminator"),
                    User.class.getMethod("getMutualGuilds")
            ));
        } catch (NoSuchMethodException e) {
            throw new UnexpectedException(e);
        }
    }
}