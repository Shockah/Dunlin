package io.shockah.dunlin.plugin;

import net.dv8tion.jda.events.DisconnectEvent;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.InviteReceivedEvent;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.ReconnectedEvent;
import net.dv8tion.jda.events.ResumedEvent;
import net.dv8tion.jda.events.ShutdownEvent;
import net.dv8tion.jda.events.StatusChangeEvent;
import net.dv8tion.jda.events.audio.AudioConnectEvent;
import net.dv8tion.jda.events.audio.AudioDisconnectEvent;
import net.dv8tion.jda.events.audio.AudioRegionChangeEvent;
import net.dv8tion.jda.events.audio.AudioTimeoutEvent;
import net.dv8tion.jda.events.audio.AudioUnableToConnectEvent;
import net.dv8tion.jda.events.audio.GenericAudioEvent;
import net.dv8tion.jda.events.channel.priv.PrivateChannelCreateEvent;
import net.dv8tion.jda.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.events.channel.text.GenericTextChannelUpdateEvent;
import net.dv8tion.jda.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.events.channel.text.TextChannelUpdateNameEvent;
import net.dv8tion.jda.events.channel.text.TextChannelUpdatePermissionsEvent;
import net.dv8tion.jda.events.channel.text.TextChannelUpdatePositionEvent;
import net.dv8tion.jda.events.channel.text.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.events.channel.voice.GenericVoiceChannelEvent;
import net.dv8tion.jda.events.channel.voice.GenericVoiceChannelUpdateEvent;
import net.dv8tion.jda.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.events.channel.voice.VoiceChannelUpdateBitrateEvent;
import net.dv8tion.jda.events.channel.voice.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.events.channel.voice.VoiceChannelUpdatePermissionsEvent;
import net.dv8tion.jda.events.channel.voice.VoiceChannelUpdatePositionEvent;
import net.dv8tion.jda.events.channel.voice.VoiceChannelUpdateUserLimitEvent;
import net.dv8tion.jda.events.guild.GenericGuildEvent;
import net.dv8tion.jda.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.events.guild.GuildUnavailableEvent;
import net.dv8tion.jda.events.guild.GuildUpdateEvent;
import net.dv8tion.jda.events.guild.UnavailableGuildJoinedEvent;
import net.dv8tion.jda.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberBanEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberUnbanEvent;
import net.dv8tion.jda.events.guild.role.GenericGuildRoleUpdateEvent;
import net.dv8tion.jda.events.guild.role.GuildRoleCreateEvent;
import net.dv8tion.jda.events.guild.role.GuildRoleDeleteEvent;
import net.dv8tion.jda.events.guild.role.GuildRoleUpdateColorEvent;
import net.dv8tion.jda.events.guild.role.GuildRoleUpdateEvent;
import net.dv8tion.jda.events.guild.role.GuildRoleUpdateGroupedEvent;
import net.dv8tion.jda.events.guild.role.GuildRoleUpdateNameEvent;
import net.dv8tion.jda.events.guild.role.GuildRoleUpdatePermissionEvent;
import net.dv8tion.jda.events.guild.role.GuildRoleUpdatePositionEvent;
import net.dv8tion.jda.events.message.GenericMessageEvent;
import net.dv8tion.jda.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.events.message.MessageDeleteEvent;
import net.dv8tion.jda.events.message.MessageEmbedEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.message.MessageUpdateEvent;
import net.dv8tion.jda.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageEmbedEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageDeleteEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageEmbedEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.events.user.GenericUserEvent;
import net.dv8tion.jda.events.user.UserAvatarUpdateEvent;
import net.dv8tion.jda.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.events.user.UserNameUpdateEvent;
import net.dv8tion.jda.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.events.user.UserTypingEvent;
import net.dv8tion.jda.events.voice.GenericVoiceEvent;
import net.dv8tion.jda.events.voice.VoiceDeafEvent;
import net.dv8tion.jda.events.voice.VoiceJoinEvent;
import net.dv8tion.jda.events.voice.VoiceLeaveEvent;
import net.dv8tion.jda.events.voice.VoiceMuteEvent;
import net.dv8tion.jda.events.voice.VoiceSelfDeafEvent;
import net.dv8tion.jda.events.voice.VoiceSelfMuteEvent;
import net.dv8tion.jda.events.voice.VoiceServerDeafEvent;
import net.dv8tion.jda.events.voice.VoiceServerMuteEvent;
import net.dv8tion.jda.hooks.EventListener;
import net.dv8tion.jda.hooks.ListenerAdapter;

public abstract class ListenerPlugin extends Plugin {
	public final EventListener listener;
	
	public ListenerPlugin(PluginManager manager, Plugin.Info info) {
		super(manager, info);
		listener = new MyListener(this);
	}
	
	protected void onEvent(Event e) { }
	
	//JDA Events
    protected void onReady(ReadyEvent event) {}
    protected void onResume(ResumedEvent event) {}
    protected void onReconnect(ReconnectedEvent event) {}
    protected void onDisconnect(DisconnectEvent event) {}
    protected void onShutdown(ShutdownEvent event) {}
    protected void onStatusChange(StatusChangeEvent event) {}

    //User Events
    protected void onUserNameUpdate(UserNameUpdateEvent event) {}
    protected void onUserAvatarUpdate(UserAvatarUpdateEvent event) {}
    protected void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {}
    protected void onUserGameUpdate(UserGameUpdateEvent event) {}
    protected void onUserTyping(UserTypingEvent event) {}

    //Message Events
    //Guild (TextChannel) Message Events
    protected void onGuildMessageReceived(GuildMessageReceivedEvent event) {}
    protected void onGuildMessageUpdate(GuildMessageUpdateEvent event) {}
    protected void onGuildMessageDelete(GuildMessageDeleteEvent event) {}
    protected void onGuildMessageEmbed(GuildMessageEmbedEvent event) {}
    //Private Message Events
    protected void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {}
    protected void onPrivateMessageUpdate(PrivateMessageUpdateEvent event) {}
    protected void onPrivateMessageDelete(PrivateMessageDeleteEvent event) {}
    protected void onPrivateMessageEmbed(PrivateMessageEmbedEvent event) {}
    //Combined Message Events (Combines Guild and Private message into 1 event)
    protected void onMessageReceived(MessageReceivedEvent event) {}
    protected void onMessageUpdate(MessageUpdateEvent event) {}
    protected void onMessageDelete(MessageDeleteEvent event) {}
    protected void onMessageBulkDelete(MessageBulkDeleteEvent event) {}
    protected void onMessageEmbed(MessageEmbedEvent event) {}

    protected void onInviteReceived(InviteReceivedEvent event) {}

    //TextChannel Events
    protected void onTextChannelDelete(TextChannelDeleteEvent event) {}
    protected void onTextChannelUpdateName(TextChannelUpdateNameEvent event) {}
    protected void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent event) {}
    protected void onTextChannelUpdatePosition(TextChannelUpdatePositionEvent event) {}
    protected void onTextChannelUpdatePermissions(TextChannelUpdatePermissionsEvent event) {}
    protected void onTextChannelCreate(TextChannelCreateEvent event) {}

    //VoiceChannel Events
    protected void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {}
    protected void onVoiceChannelUpdateName(VoiceChannelUpdateNameEvent event) {}
    protected void onVoiceChannelUpdatePosition(VoiceChannelUpdatePositionEvent event) {}
    protected void onVoiceChannelUpdateUserLimit(VoiceChannelUpdateUserLimitEvent event) {}
    protected void onVoiceChannelUpdateBitrate(VoiceChannelUpdateBitrateEvent event) {}
    protected void onVoiceChannelUpdatePermissions(VoiceChannelUpdatePermissionsEvent event) {}
    protected void onVoiceChannelCreate(VoiceChannelCreateEvent event) {}

    //PrivateChannel Events
    protected void onPrivateChannelCreate(PrivateChannelCreateEvent event) {}

    //Guild Events
    protected void onGuildJoin(GuildJoinEvent event) {}
    protected void onUnavailGuildJoined(UnavailableGuildJoinedEvent event) {}
    protected void onGuildUpdate(GuildUpdateEvent event) {}
    protected void onGuildLeave(GuildLeaveEvent event) {}
    protected void onGuildAvailable(GuildAvailableEvent event) {}
    protected void onGuildUnavailable(GuildUnavailableEvent event) {}
    protected void onGuildMemberJoin(GuildMemberJoinEvent event) {}
    protected void onGuildMemberLeave(GuildMemberLeaveEvent event) {}
    protected void onGuildMemberBan(GuildMemberBanEvent event) {}
    protected void onGuildMemberUnban(GuildMemberUnbanEvent event) {}
    protected void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {}
    protected void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {}
    protected void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {}
    protected void onGuildRoleCreate(GuildRoleCreateEvent event) {}
    protected void onGuildRoleDelete(GuildRoleDeleteEvent event) {}

    //Guild Role Update Events
    protected void onGuildRoleUpdate(GuildRoleUpdateEvent event) {}
    protected void onGuildRoleUpdateName(GuildRoleUpdateNameEvent event) {}
    protected void onGuildRoleUpdateColor(GuildRoleUpdateColorEvent event) {}
    protected void onGuildRoleUpdatePosition(GuildRoleUpdatePositionEvent event) {}
    protected void onGuildRoleUpdatePermission(GuildRoleUpdatePermissionEvent event) {}
    protected void onGuildRoleUpdateGrouped(GuildRoleUpdateGroupedEvent event) {}

    //VoiceStatus Events
    protected void onVoiceSelfMute(VoiceSelfMuteEvent event) {}
    protected void onVoiceSelfDeaf(VoiceSelfDeafEvent event) {}
    protected void onVoiceServerMute(VoiceServerMuteEvent event) {}
    protected void onVoiceServerDeaf(VoiceServerDeafEvent event) {}
    protected void onVoiceMute(VoiceMuteEvent event) {}
    protected void onVoiceDeaf(VoiceDeafEvent event) {}
    protected void onVoiceJoin(VoiceJoinEvent event) {}
    protected void onVoiceLeave(VoiceLeaveEvent event) {}

    //Audio System Events
    protected void onAudioConnect(AudioConnectEvent event) {}
    protected void onAudioDisconnect(AudioDisconnectEvent event) {}
    protected void onAudioUnableToConnect(AudioUnableToConnectEvent event) {}
    protected void onAudioTimeout(AudioTimeoutEvent event) {}
    protected void onAudioRegionChange(AudioRegionChangeEvent event) {}

    //Generic Events
    protected void onGenericUserEvent(GenericUserEvent event) {}
    protected void onGenericMessage(GenericMessageEvent event) {}
    protected void onGenericGuildMessage(GenericGuildMessageEvent event) {}
    protected void onGenericPrivateMessage(GenericPrivateMessageEvent event) {}
    protected void onGenericTextChannel(GenericTextChannelEvent event) {}
    protected void onGenericTextChannelUpdate(GenericTextChannelUpdateEvent event) {}
    protected void onGenericVoiceChannel(GenericVoiceChannelEvent event) {}
    protected void onGenericVoiceChannelUpdate(GenericVoiceChannelUpdateEvent event) {}
    protected void onGenericGuildMember(GenericGuildMemberEvent event) {}
    protected void onGenericGuild(GenericGuildEvent event) {}
    protected void onGenericVoice(GenericVoiceEvent event) {}
    protected void onGenericAudio(GenericAudioEvent event) {}
    protected void onGenericGuildRoleUpdate(GenericGuildRoleUpdateEvent event) {}
	
	protected class MyListener extends ListenerAdapter {
		public final ListenerPlugin plugin;
		
		public MyListener(ListenerPlugin plugin) {
			this.plugin = plugin;
		}

		@Override
		public void onEvent(Event event) {
			try {
				super.onEvent(event);
				plugin.onEvent(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//JDA Events
	    @Override public void onReady(ReadyEvent event) { plugin.onReady(event); }
	    @Override public void onResume(ResumedEvent event) { plugin.onResume(event); }
	    @Override public void onReconnect(ReconnectedEvent event) { plugin.onReconnect(event); }
	    @Override public void onDisconnect(DisconnectEvent event) { plugin.onDisconnect(event); }
	    @Override public void onShutdown(ShutdownEvent event) { plugin.onShutdown(event); }
	    @Override public void onStatusChange(StatusChangeEvent event) { plugin.onStatusChange(event); }

	    //User Events
	    @Override public void onUserNameUpdate(UserNameUpdateEvent event) { plugin.onUserNameUpdate(event); }
	    @Override public void onUserAvatarUpdate(UserAvatarUpdateEvent event) { plugin.onUserAvatarUpdate(event); }
	    @Override public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) { plugin.onUserOnlineStatusUpdate(event); }
	    @Override public void onUserGameUpdate(UserGameUpdateEvent event) { plugin.onUserGameUpdate(event); }
	    @Override public void onUserTyping(UserTypingEvent event) { plugin.onUserTyping(event); }

	    //Message Events
	    //Guild (TextChannel) Message Events
	    @Override public void onGuildMessageReceived(GuildMessageReceivedEvent event) { plugin.onGuildMessageReceived(event); }
	    @Override public void onGuildMessageUpdate(GuildMessageUpdateEvent event) { plugin.onGuildMessageUpdate(event); }
	    @Override public void onGuildMessageDelete(GuildMessageDeleteEvent event) { plugin.onGuildMessageDelete(event); }
	    @Override public void onGuildMessageEmbed(GuildMessageEmbedEvent event) { plugin.onGuildMessageEmbed(event); }
	    //Private Message Events
	    @Override public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) { plugin.onPrivateMessageReceived(event); }
	    @Override public void onPrivateMessageUpdate(PrivateMessageUpdateEvent event) { plugin.onPrivateMessageUpdate(event); }
	    @Override public void onPrivateMessageDelete(PrivateMessageDeleteEvent event) { plugin.onPrivateMessageDelete(event); }
	    @Override public void onPrivateMessageEmbed(PrivateMessageEmbedEvent event) { plugin.onPrivateMessageEmbed(event); }
	    //Combined Message Events (Combines Guild and Private message into 1 event)
	    @Override public void onMessageReceived(MessageReceivedEvent event) { plugin.onMessageReceived(event); }
	    @Override public void onMessageUpdate(MessageUpdateEvent event) { plugin.onMessageUpdate(event); }
	    @Override public void onMessageDelete(MessageDeleteEvent event) { plugin.onMessageDelete(event); }
	    @Override public void onMessageBulkDelete(MessageBulkDeleteEvent event) { plugin.onMessageBulkDelete(event); }
	    @Override public void onMessageEmbed(MessageEmbedEvent event) { plugin.onMessageEmbed(event); }

	    @Override public void onInviteReceived(InviteReceivedEvent event) { plugin.onInviteReceived(event); }

	    //TextChannel Events
	    @Override public void onTextChannelDelete(TextChannelDeleteEvent event) { plugin.onTextChannelDelete(event); }
	    @Override public void onTextChannelUpdateName(TextChannelUpdateNameEvent event) { plugin.onTextChannelUpdateName(event); }
	    @Override public void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent event) { plugin.onTextChannelUpdateTopic(event); }
	    @Override public void onTextChannelUpdatePosition(TextChannelUpdatePositionEvent event) { plugin.onTextChannelUpdatePosition(event); }
	    @Override public void onTextChannelUpdatePermissions(TextChannelUpdatePermissionsEvent event) { plugin.onTextChannelUpdatePermissions(event); }
	    @Override public void onTextChannelCreate(TextChannelCreateEvent event) { plugin.onTextChannelCreate(event); }

	    //VoiceChannel Events
	    @Override public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) { plugin.onVoiceChannelDelete(event); }
	    @Override public void onVoiceChannelUpdateName(VoiceChannelUpdateNameEvent event) { plugin.onVoiceChannelUpdateName(event); }
	    @Override public void onVoiceChannelUpdatePosition(VoiceChannelUpdatePositionEvent event) { plugin.onVoiceChannelUpdatePosition(event); }
	    @Override public void onVoiceChannelUpdateUserLimit(VoiceChannelUpdateUserLimitEvent event) { plugin.onVoiceChannelUpdateUserLimit(event); }
	    @Override public void onVoiceChannelUpdateBitrate(VoiceChannelUpdateBitrateEvent event) { plugin.onVoiceChannelUpdateBitrate(event); }
	    @Override public void onVoiceChannelUpdatePermissions(VoiceChannelUpdatePermissionsEvent event) { plugin.onVoiceChannelUpdatePermissions(event); }
	    @Override public void onVoiceChannelCreate(VoiceChannelCreateEvent event) { plugin.onVoiceChannelCreate(event); }

	    //PrivateChannel Events
	    @Override public void onPrivateChannelCreate(PrivateChannelCreateEvent event) { plugin.onPrivateChannelCreate(event); }

	    //Guild Events
	    @Override public void onGuildJoin(GuildJoinEvent event) { plugin.onGuildJoin(event); }
	    @Override public void onUnavailGuildJoined(UnavailableGuildJoinedEvent event) { plugin.onUnavailGuildJoined(event); }
	    @Override public void onGuildUpdate(GuildUpdateEvent event) { plugin.onGuildUpdate(event); }
	    @Override public void onGuildLeave(GuildLeaveEvent event) { plugin.onGuildLeave(event); }
	    @Override public void onGuildAvailable(GuildAvailableEvent event) { plugin.onGuildAvailable(event); }
	    @Override public void onGuildUnavailable(GuildUnavailableEvent event) { plugin.onGuildUnavailable(event); }
	    @Override public void onGuildMemberJoin(GuildMemberJoinEvent event) { plugin.onGuildMemberJoin(event); }
	    @Override public void onGuildMemberLeave(GuildMemberLeaveEvent event) { plugin.onGuildMemberLeave(event); }
	    @Override public void onGuildMemberBan(GuildMemberBanEvent event) { plugin.onGuildMemberBan(event); }
	    @Override public void onGuildMemberUnban(GuildMemberUnbanEvent event) { plugin.onGuildMemberUnban(event); }
	    @Override public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) { plugin.onGuildMemberRoleAdd(event); }
	    @Override public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) { plugin.onGuildMemberRoleRemove(event); }
	    @Override public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) { plugin.onGuildMemberNickChange(event); }
	    @Override public void onGuildRoleCreate(GuildRoleCreateEvent event) { plugin.onGuildRoleCreate(event); }
	    @Override public void onGuildRoleDelete(GuildRoleDeleteEvent event) { plugin.onGuildRoleDelete(event); }

	    //Guild Role Update Events
	    @Override public void onGuildRoleUpdate(GuildRoleUpdateEvent event) { plugin.onGuildRoleUpdate(event); }
	    @Override public void onGuildRoleUpdateName(GuildRoleUpdateNameEvent event) { plugin.onGuildRoleUpdateName(event); }
	    @Override public void onGuildRoleUpdateColor(GuildRoleUpdateColorEvent event) { plugin.onGuildRoleUpdateColor(event); }
	    @Override public void onGuildRoleUpdatePosition(GuildRoleUpdatePositionEvent event) { plugin.onGuildRoleUpdatePosition(event); }
	    @Override public void onGuildRoleUpdatePermission(GuildRoleUpdatePermissionEvent event) { plugin.onGuildRoleUpdatePermission(event); }
	    @Override public void onGuildRoleUpdateGrouped(GuildRoleUpdateGroupedEvent event) { plugin.onGuildRoleUpdateGrouped(event); }

	    //VoiceStatus Events
	    @Override public void onVoiceSelfMute(VoiceSelfMuteEvent event) { plugin.onVoiceSelfMute(event); }
	    @Override public void onVoiceSelfDeaf(VoiceSelfDeafEvent event) { plugin.onVoiceSelfDeaf(event); }
	    @Override public void onVoiceServerMute(VoiceServerMuteEvent event) { plugin.onVoiceServerMute(event); }
	    @Override public void onVoiceServerDeaf(VoiceServerDeafEvent event) { plugin.onVoiceServerDeaf(event); }
	    @Override public void onVoiceMute(VoiceMuteEvent event) { plugin.onVoiceMute(event); }
	    @Override public void onVoiceDeaf(VoiceDeafEvent event) { plugin.onVoiceDeaf(event); }
	    @Override public void onVoiceJoin(VoiceJoinEvent event) { plugin.onVoiceJoin(event); }
	    @Override public void onVoiceLeave(VoiceLeaveEvent event) { plugin.onVoiceLeave(event); }

	    //Audio System Events
	    @Override public void onAudioConnect(AudioConnectEvent event) { plugin.onAudioConnect(event); }
	    @Override public void onAudioDisconnect(AudioDisconnectEvent event) { plugin.onAudioDisconnect(event); }
	    @Override public void onAudioUnableToConnect(AudioUnableToConnectEvent event) { plugin.onAudioUnableToConnect(event); }
	    @Override public void onAudioTimeout(AudioTimeoutEvent event) { plugin.onAudioTimeout(event); }
	    @Override public void onAudioRegionChange(AudioRegionChangeEvent event) { plugin.onAudioRegionChange(event); }

	    //Generic Events
	    @Override public void onGenericUserEvent(GenericUserEvent event) { plugin.onGenericUserEvent(event); }
	    @Override public void onGenericMessage(GenericMessageEvent event) { plugin.onGenericMessage(event); }
	    @Override public void onGenericGuildMessage(GenericGuildMessageEvent event) { plugin.onGenericGuildMessage(event); }
	    @Override public void onGenericPrivateMessage(GenericPrivateMessageEvent event) { plugin.onGenericPrivateMessage(event); }
	    @Override public void onGenericTextChannel(GenericTextChannelEvent event) { plugin.onGenericTextChannel(event); }
	    @Override public void onGenericTextChannelUpdate(GenericTextChannelUpdateEvent event) { plugin.onGenericTextChannelUpdate(event); }
	    @Override public void onGenericVoiceChannel(GenericVoiceChannelEvent event) { plugin.onGenericVoiceChannel(event); }
	    @Override public void onGenericVoiceChannelUpdate(GenericVoiceChannelUpdateEvent event) { plugin.onGenericVoiceChannelUpdate(event); }
	    @Override public void onGenericGuildMember(GenericGuildMemberEvent event) { plugin.onGenericGuildMember(event); }
	    @Override public void onGenericGuild(GenericGuildEvent event) { plugin.onGenericGuild(event); }
	    @Override public void onGenericVoice(GenericVoiceEvent event) { plugin.onGenericVoice(event); }
	    @Override public void onGenericAudio(GenericAudioEvent event) { plugin.onGenericAudio(event); }
	    @Override public void onGenericGuildRoleUpdate(GenericGuildRoleUpdateEvent event) { plugin.onGenericGuildRoleUpdate(event); }
	}
}