package pl.shockah.dunlin.plugin;

import net.dv8tion.jda.client.events.call.CallCreateEvent;
import net.dv8tion.jda.client.events.call.CallDeleteEvent;
import net.dv8tion.jda.client.events.call.GenericCallEvent;
import net.dv8tion.jda.client.events.call.update.CallUpdateRegionEvent;
import net.dv8tion.jda.client.events.call.update.CallUpdateRingingUsersEvent;
import net.dv8tion.jda.client.events.call.update.GenericCallUpdateEvent;
import net.dv8tion.jda.client.events.call.voice.CallVoiceJoinEvent;
import net.dv8tion.jda.client.events.call.voice.CallVoiceLeaveEvent;
import net.dv8tion.jda.client.events.call.voice.CallVoiceSelfDeafenEvent;
import net.dv8tion.jda.client.events.call.voice.CallVoiceSelfMuteEvent;
import net.dv8tion.jda.client.events.call.voice.GenericCallVoiceEvent;
import net.dv8tion.jda.client.events.group.GenericGroupEvent;
import net.dv8tion.jda.client.events.group.GroupJoinEvent;
import net.dv8tion.jda.client.events.group.GroupLeaveEvent;
import net.dv8tion.jda.client.events.group.GroupUserJoinEvent;
import net.dv8tion.jda.client.events.group.GroupUserLeaveEvent;
import net.dv8tion.jda.client.events.group.update.GenericGroupUpdateEvent;
import net.dv8tion.jda.client.events.group.update.GroupUpdateIconEvent;
import net.dv8tion.jda.client.events.group.update.GroupUpdateNameEvent;
import net.dv8tion.jda.client.events.group.update.GroupUpdateOwnerEvent;
import net.dv8tion.jda.client.events.message.group.GenericGroupMessageEvent;
import net.dv8tion.jda.client.events.message.group.GroupMessageDeleteEvent;
import net.dv8tion.jda.client.events.message.group.GroupMessageEmbedEvent;
import net.dv8tion.jda.client.events.message.group.GroupMessageReceivedEvent;
import net.dv8tion.jda.client.events.message.group.GroupMessageUpdateEvent;
import net.dv8tion.jda.client.events.relationship.FriendAddedEvent;
import net.dv8tion.jda.client.events.relationship.FriendRemovedEvent;
import net.dv8tion.jda.client.events.relationship.FriendRequestCanceledEvent;
import net.dv8tion.jda.client.events.relationship.FriendRequestIgnoredEvent;
import net.dv8tion.jda.client.events.relationship.FriendRequestReceivedEvent;
import net.dv8tion.jda.client.events.relationship.FriendRequestSentEvent;
import net.dv8tion.jda.client.events.relationship.GenericRelationshipAddEvent;
import net.dv8tion.jda.client.events.relationship.GenericRelationshipEvent;
import net.dv8tion.jda.client.events.relationship.GenericRelationshipRemoveEvent;
import net.dv8tion.jda.client.events.relationship.UserBlockedEvent;
import net.dv8tion.jda.client.events.relationship.UserUnblockedEvent;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.events.channel.priv.PrivateChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.priv.PrivateChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.update.GenericTextChannelUpdateEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdatePermissionsEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdatePositionEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.core.events.channel.voice.GenericVoiceChannelEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.update.GenericVoiceChannelUpdateEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateBitrateEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdatePermissionsEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdatePositionEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateUserLimitEvent;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.GuildUnavailableEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.UnavailableGuildJoinedEvent;
import net.dv8tion.jda.core.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.guild.update.GenericGuildUpdateEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateAfkChannelEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateAfkTimeoutEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateMFALevelEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNotificationLevelEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateRegionEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateSplashEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateVerificationLevelEvent;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceSelfDeafenEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceSelfMuteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceSuppressEvent;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageEmbedEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageEmbedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageEmbedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.role.GenericRoleEvent;
import net.dv8tion.jda.core.events.role.RoleCreateEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.GenericRoleUpdateEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateHoistedEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateMentionableEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdatePositionEvent;
import net.dv8tion.jda.core.events.self.GenericSelfUpdateEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateAvatarEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateEmailEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateMFAEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateVerifiedEvent;
import net.dv8tion.jda.core.events.user.GenericUserEvent;
import net.dv8tion.jda.core.events.user.UserAvatarUpdateEvent;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.events.user.UserNameUpdateEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.events.user.UserTypingEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import pl.shockah.plugin.PluginInfo;

public abstract class ListenerPlugin extends Plugin {
	public final EventListener listener;
	
	public ListenerPlugin(PluginManager manager, PluginInfo info) {
		super(manager, info);
		listener = new MyListener(this);
	}
	
	protected void onGenericEvent(Event e) { }
	
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

	//Self Events. Fires only in relation to the currently logged in account.
	protected void onSelfUpdateAvatar(SelfUpdateAvatarEvent event) {}
	protected void onSelfUpdateEmail(SelfUpdateEmailEvent event) {}
	protected void onSelfUpdateMFA(SelfUpdateMFAEvent event) {}
	protected void onSelfUpdateName(SelfUpdateNameEvent event) {}
	protected void onSelfUpdateVerified(SelfUpdateVerifiedEvent event) {}

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
	protected void onMessageReactionAdd(MessageReactionAddEvent event) {}
	protected void onMessageReactionRemove(MessageReactionRemoveEvent event) {}
	protected void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent event) {}

//protected void onInviteReceived(InviteReceivedEvent event) {}

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
	protected void onPrivateChannelDelete(PrivateChannelDeleteEvent event) {}

	//Guild Events
	protected void onGuildJoin(GuildJoinEvent event) {}
	protected void onGuildLeave(GuildLeaveEvent event) {}
	protected void onGuildAvailable(GuildAvailableEvent event) {}
	protected void onGuildUnavailable(GuildUnavailableEvent event) {}
	protected void onUnavailableGuildJoined(UnavailableGuildJoinedEvent event) {}
	protected void onGuildBan(GuildBanEvent event) {}
	protected void onGuildUnban(GuildUnbanEvent event) {}

	//Guild Update Events
	protected void onGuildUpdateAfkChannel(GuildUpdateAfkChannelEvent event) {}
	protected void onGuildUpdateAfkTimeout(GuildUpdateAfkTimeoutEvent event) {}
	protected void onGuildUpdateIcon(GuildUpdateIconEvent event) {}
	protected void onGuildUpdateMFALevel(GuildUpdateMFALevelEvent event) {}
	protected void onGuildUpdateName(GuildUpdateNameEvent event){}
	protected void onGuildUpdateNotificationLevel(GuildUpdateNotificationLevelEvent event) {}
	protected void onGuildUpdateOwner(GuildUpdateOwnerEvent event) {}
	protected void onGuildUpdateRegion(GuildUpdateRegionEvent event) {}
	protected void onGuildUpdateSplash(GuildUpdateSplashEvent event) {}
	protected void onGuildUpdateVerificationLevel(GuildUpdateVerificationLevelEvent event) {}

	//Guild Member Events
	protected void onGuildMemberJoin(GuildMemberJoinEvent event) {}
	protected void onGuildMemberLeave(GuildMemberLeaveEvent event) {}
	protected void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {}
	protected void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {}
	protected void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {}

	//Guild Voice Events
	protected void onGuildVoiceJoin(GuildVoiceJoinEvent event) {}
	protected void onGuildVoiceMove(GuildVoiceMoveEvent event) {}
	protected void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {}
	protected void onGuildVoiceMute(GuildVoiceMuteEvent event) {}
	protected void onGuildVoiceDeafen(GuildVoiceDeafenEvent event) {}
	protected void onGuildVoiceGuildMute(GuildVoiceGuildMuteEvent event) {}
	protected void onGuildVoiceGuildDeafen(GuildVoiceGuildDeafenEvent event) {}
	protected void onGuildVoiceSelfMute(GuildVoiceSelfMuteEvent event) {}
	protected void onGuildVoiceSelfDeafen(GuildVoiceSelfDeafenEvent event) {}
	protected void onGuildVoiceSuppress(GuildVoiceSuppressEvent event) {}

	//Role events
	protected void onRoleCreate(RoleCreateEvent event) {}
	protected void onRoleDelete(RoleDeleteEvent event) {}

	//Role Update Events
	protected void onRoleUpdateColor(RoleUpdateColorEvent event) {}
	protected void onRoleUpdateHoisted(RoleUpdateHoistedEvent event) {}
	protected void onRoleUpdateMentionable(RoleUpdateMentionableEvent event) {}
	protected void onRoleUpdateName(RoleUpdateNameEvent event) {}
	protected void onRoleUpdatePermissions(RoleUpdatePermissionsEvent event) {}
	protected void onRoleUpdatePosition(RoleUpdatePositionEvent event) {}

////Audio System Events
//protected void onAudioConnect(AudioConnectEvent event) {}
//protected void onAudioDisconnect(AudioDisconnectEvent event) {}
//protected void onAudioUnableToConnect(AudioUnableToConnectEvent event) {}
//protected void onAudioTimeout(AudioTimeoutEvent event) {}
//protected void onAudioRegionChange(AudioRegionChangeEvent event) {}

	//Generic Events
	protected void onGenericMessage(GenericMessageEvent event) {}
	protected void onGenericMessageReaction(GenericMessageReactionEvent event) {}
	protected void onGenericGuildMessage(GenericGuildMessageEvent event) {}
	protected void onGenericPrivateMessage(GenericPrivateMessageEvent event) {}
	protected void onGenericUser(GenericUserEvent event) {}
	protected void onGenericSelfUpdate(GenericSelfUpdateEvent event) {}
	protected void onGenericTextChannel(GenericTextChannelEvent event) {}
	protected void onGenericTextChannelUpdate(GenericTextChannelUpdateEvent event) {}
	protected void onGenericVoiceChannel(GenericVoiceChannelEvent event) {}
	protected void onGenericVoiceChannelUpdate(GenericVoiceChannelUpdateEvent event) {}
	protected void onGenericGuild(GenericGuildEvent event) {}
	protected void onGenericGuildUpdate(GenericGuildUpdateEvent event) {}
	protected void onGenericGuildMember(GenericGuildMemberEvent event) {}
	protected void onGenericGuildVoice(GenericGuildVoiceEvent event) {}
	protected void onGenericRole(GenericRoleEvent event) {}
	protected void onGenericRoleUpdate(GenericRoleUpdateEvent event) {}
//protected void onGenericAudio(GenericAudioEvent event) {}


	// ==========================================================================================
	// |                                   Client Only Events                                   |
	// ==========================================================================================

	//Relationship Events
	protected void onFriendAdded(FriendAddedEvent event) {}
	protected void onFriendRemoved(FriendRemovedEvent event) {}
	protected void onUserBlocked(UserBlockedEvent event) {}
	protected void onUserUnblocked(UserUnblockedEvent event) {}
	protected void onFriendRequestSent(FriendRequestSentEvent event) {}
	protected void onFriendRequestCanceled(FriendRequestCanceledEvent event) {}
	protected void onFriendRequestReceived(FriendRequestReceivedEvent event) {}
	protected void onFriendRequestIgnored(FriendRequestIgnoredEvent event) {}

	//Group Events
	protected void onGroupJoin(GroupJoinEvent event) {}
	protected void onGroupLeave(GroupLeaveEvent event) {}
	protected void onGroupUserJoin(GroupUserJoinEvent event) {}
	protected void onGroupUserLeave(GroupUserLeaveEvent event) {}

	//Group Message Events
	protected void onGroupMessageReceived(GroupMessageReceivedEvent event) {}
	protected void onGroupMessageUpdate(GroupMessageUpdateEvent event) {}
	protected void onGroupMessageDelete(GroupMessageDeleteEvent event) {}
	protected void onGroupMessageEmbed(GroupMessageEmbedEvent event) {}

	//Group Update Events
	protected void onGroupUpdateIcon(GroupUpdateIconEvent event) {}
	protected void onGroupUpdateName(GroupUpdateNameEvent event){}
	protected void onGroupUpdateOwner(GroupUpdateOwnerEvent event) {}

	//Call Events
	protected void onCallCreate(CallCreateEvent event) {}
	protected void onCallDelete(CallDeleteEvent event) {}

	//Call Update Events
	protected void onCallUpdateRegion(CallUpdateRegionEvent event) {}
	protected void onCallUpdateRingingUsers(CallUpdateRingingUsersEvent event) {}

	//Call Voice Events
	protected void onCallVoiceJoin(CallVoiceJoinEvent event) {}
	protected void onCallVoiceLeave(CallVoiceLeaveEvent event) {}
	protected void onCallVoiceSelfMute(CallVoiceSelfMuteEvent event) {}
	protected void onCallVoiceSelfDeafen(CallVoiceSelfDeafenEvent event) {}

	//Client Only Generic Events
	protected void onGenericRelationship(GenericRelationshipEvent event) {}
	protected void onGenericRelationshipAdd(GenericRelationshipAddEvent event) {}
	protected void onGenericRelationshipRemove(GenericRelationshipRemoveEvent event) {}
	protected void onGenericGroup(GenericGroupEvent event) {}
	protected void onGenericGroupMessage(GenericGroupMessageEvent event) {}
	protected void onGenericGroupUpdate(GenericGroupUpdateEvent event) {}
	protected void onGenericCall(GenericCallEvent event) {}
	protected void onGenericCallUpdate(GenericCallUpdateEvent event) {}
	protected void onGenericCallVoice(GenericCallVoiceEvent event) {}
	
	protected class MyListener extends ListenerAdapter {
		public final ListenerPlugin plugin;
		
		public MyListener(ListenerPlugin plugin) {
			this.plugin = plugin;
		}

		@Override
		public void onGenericEvent(Event event) {
			try {
				super.onGenericEvent(event);
				plugin.onGenericEvent(event);
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

		//Self Events. Fires only in relation to the currently logged in account.
		@Override public void onSelfUpdateAvatar(SelfUpdateAvatarEvent event) { plugin.onSelfUpdateAvatar(event); }
		@Override public void onSelfUpdateEmail(SelfUpdateEmailEvent event) { plugin.onSelfUpdateEmail(event); }
		@Override public void onSelfUpdateMFA(SelfUpdateMFAEvent event) { plugin.onSelfUpdateMFA(event); }
		@Override public void onSelfUpdateName(SelfUpdateNameEvent event) { plugin.onSelfUpdateName(event); }
		@Override public void onSelfUpdateVerified(SelfUpdateVerifiedEvent event) { plugin.onSelfUpdateVerified(event); }

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
		@Override public void onMessageReactionAdd(MessageReactionAddEvent event) { plugin.onMessageReactionAdd(event); }
		@Override public void onMessageReactionRemove(MessageReactionRemoveEvent event) { plugin.onMessageReactionRemove(event); }
		@Override public void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent event) { plugin.onMessageReactionRemoveAll(event); }

		//@Override public void onInviteReceived(InviteReceivedEvent event) { plugin.onInviteReceived(event); }

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
		@Override public void onPrivateChannelDelete(PrivateChannelDeleteEvent event) { plugin.onPrivateChannelDelete(event); }

		//Guild Events
		@Override public void onGuildJoin(GuildJoinEvent event) { plugin.onGuildJoin(event); }
		@Override public void onGuildLeave(GuildLeaveEvent event) { plugin.onGuildLeave(event); }
		@Override public void onGuildAvailable(GuildAvailableEvent event) { plugin.onGuildAvailable(event); }
		@Override public void onGuildUnavailable(GuildUnavailableEvent event) { plugin.onGuildUnavailable(event); }
		@Override public void onUnavailableGuildJoined(UnavailableGuildJoinedEvent event) { plugin.onUnavailableGuildJoined(event); }
		@Override public void onGuildBan(GuildBanEvent event) { plugin.onGuildBan(event); }
		@Override public void onGuildUnban(GuildUnbanEvent event) { plugin.onGuildUnban(event); }

		//Guild Update Events
		@Override public void onGuildUpdateAfkChannel(GuildUpdateAfkChannelEvent event) { plugin.onGuildUpdateAfkChannel(event); }
		@Override public void onGuildUpdateAfkTimeout(GuildUpdateAfkTimeoutEvent event) { plugin.onGuildUpdateAfkTimeout(event); }
		@Override public void onGuildUpdateIcon(GuildUpdateIconEvent event) { plugin.onGuildUpdateIcon(event); }
		@Override public void onGuildUpdateMFALevel(GuildUpdateMFALevelEvent event) { plugin.onGuildUpdateMFALevel(event); }
		@Override public void onGuildUpdateName(GuildUpdateNameEvent event){}
		@Override public void onGuildUpdateNotificationLevel(GuildUpdateNotificationLevelEvent event) { plugin.onGuildUpdateNotificationLevel(event); }
		@Override public void onGuildUpdateOwner(GuildUpdateOwnerEvent event) { plugin.onGuildUpdateOwner(event); }
		@Override public void onGuildUpdateRegion(GuildUpdateRegionEvent event) { plugin.onGuildUpdateRegion(event); }
		@Override public void onGuildUpdateSplash(GuildUpdateSplashEvent event) { plugin.onGuildUpdateSplash(event); }
		@Override public void onGuildUpdateVerificationLevel(GuildUpdateVerificationLevelEvent event) { plugin.onGuildUpdateVerificationLevel(event); }

		//Guild Member Events
		@Override public void onGuildMemberJoin(GuildMemberJoinEvent event) { plugin.onGuildMemberJoin(event); }
		@Override public void onGuildMemberLeave(GuildMemberLeaveEvent event) { plugin.onGuildMemberLeave(event); }
		@Override public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) { plugin.onGuildMemberRoleAdd(event); }
		@Override public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) { plugin.onGuildMemberRoleRemove(event); }
		@Override public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) { plugin.onGuildMemberNickChange(event); }

		//Guild Voice Events
		@Override public void onGuildVoiceJoin(GuildVoiceJoinEvent event) { plugin.onGuildVoiceJoin(event); }
		@Override public void onGuildVoiceMove(GuildVoiceMoveEvent event) { plugin.onGuildVoiceMove(event); }
		@Override public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) { plugin.onGuildVoiceLeave(event); }
		@Override public void onGuildVoiceMute(GuildVoiceMuteEvent event) { plugin.onGuildVoiceMute(event); }
		@Override public void onGuildVoiceDeafen(GuildVoiceDeafenEvent event) { plugin.onGuildVoiceDeafen(event); }
		@Override public void onGuildVoiceGuildMute(GuildVoiceGuildMuteEvent event) { plugin.onGuildVoiceGuildMute(event); }
		@Override public void onGuildVoiceGuildDeafen(GuildVoiceGuildDeafenEvent event) { plugin.onGuildVoiceGuildDeafen(event); }
		@Override public void onGuildVoiceSelfMute(GuildVoiceSelfMuteEvent event) { plugin.onGuildVoiceSelfMute(event); }
		@Override public void onGuildVoiceSelfDeafen(GuildVoiceSelfDeafenEvent event) { plugin.onGuildVoiceSelfDeafen(event); }
		@Override public void onGuildVoiceSuppress(GuildVoiceSuppressEvent event) { plugin.onGuildVoiceSuppress(event); }

		//Role events
		@Override public void onRoleCreate(RoleCreateEvent event) { plugin.onRoleCreate(event); }
		@Override public void onRoleDelete(RoleDeleteEvent event) { plugin.onRoleDelete(event); }

		//Role Update Events
		@Override public void onRoleUpdateColor(RoleUpdateColorEvent event) { plugin.onRoleUpdateColor(event); }
		@Override public void onRoleUpdateHoisted(RoleUpdateHoistedEvent event) { plugin.onRoleUpdateHoisted(event); }
		@Override public void onRoleUpdateMentionable(RoleUpdateMentionableEvent event) { plugin.onRoleUpdateMentionable(event); }
		@Override public void onRoleUpdateName(RoleUpdateNameEvent event) { plugin.onRoleUpdateName(event); }
		@Override public void onRoleUpdatePermissions(RoleUpdatePermissionsEvent event) { plugin.onRoleUpdatePermissions(event); }
		@Override public void onRoleUpdatePosition(RoleUpdatePositionEvent event) { plugin.onRoleUpdatePosition(event); }

		////Audio System Events
		// @Override public void onAudioConnect(AudioConnectEvent event) { plugin.onAudioConnect(event); }
		//@Override public void onAudioDisconnect(AudioDisconnectEvent event) { plugin.onAudioDisconnect(event); }
		//@Override public void onAudioUnableToConnect(AudioUnableToConnectEvent event) { plugin.onAudioUnableToConnect(event); }
		//@Override public void onAudioTimeout(AudioTimeoutEvent event) { plugin.onAudioTimeout(event); }
		//@Override public void onAudioRegionChange(AudioRegionChangeEvent event) { plugin.onAudioRegionChange(event); }

		//Generic Events
		@Override public void onGenericMessage(GenericMessageEvent event) { plugin.onGenericMessage(event); }
		@Override public void onGenericMessageReaction(GenericMessageReactionEvent event) { plugin.onGenericMessageReaction(event); }
		@Override public void onGenericGuildMessage(GenericGuildMessageEvent event) { plugin.onGenericGuildMessage(event); }
		@Override public void onGenericPrivateMessage(GenericPrivateMessageEvent event) { plugin.onGenericPrivateMessage(event); }
		@Override public void onGenericUser(GenericUserEvent event) { plugin.onGenericUser(event); }
		@Override public void onGenericSelfUpdate(GenericSelfUpdateEvent event) { plugin.onGenericSelfUpdate(event); }
		@Override public void onGenericTextChannel(GenericTextChannelEvent event) { plugin.onGenericTextChannel(event); }
		@Override public void onGenericTextChannelUpdate(GenericTextChannelUpdateEvent event) { plugin.onGenericTextChannelUpdate(event); }
		@Override public void onGenericVoiceChannel(GenericVoiceChannelEvent event) { plugin.onGenericVoiceChannel(event); }
		@Override public void onGenericVoiceChannelUpdate(GenericVoiceChannelUpdateEvent event) { plugin.onGenericVoiceChannelUpdate(event); }
		@Override public void onGenericGuild(GenericGuildEvent event) { plugin.onGenericGuild(event); }
		@Override public void onGenericGuildUpdate(GenericGuildUpdateEvent event) { plugin.onGenericGuildUpdate(event); }
		@Override public void onGenericGuildMember(GenericGuildMemberEvent event) { plugin.onGenericGuildMember(event); }
		@Override public void onGenericGuildVoice(GenericGuildVoiceEvent event) { plugin.onGenericGuildVoice(event); }
		@Override public void onGenericRole(GenericRoleEvent event) { plugin.onGenericRole(event); }
		@Override public void onGenericRoleUpdate(GenericRoleUpdateEvent event) { plugin.onGenericRoleUpdate(event); }
		//@Override public void onGenericAudio(GenericAudioEvent event) { plugin.onGenericAudio(event); }


		// ==========================================================================================
		// |                                   Client Only Events                                   |
		// ==========================================================================================

		//Relationship Events
		@Override public void onFriendAdded(FriendAddedEvent event) { plugin.onFriendAdded(event); }
		@Override public void onFriendRemoved(FriendRemovedEvent event) { plugin.onFriendRemoved(event); }
		@Override public void onUserBlocked(UserBlockedEvent event) { plugin.onUserBlocked(event); }
		@Override public void onUserUnblocked(UserUnblockedEvent event) { plugin.onUserUnblocked(event); }
		@Override public void onFriendRequestSent(FriendRequestSentEvent event) { plugin.onFriendRequestSent(event); }
		@Override public void onFriendRequestCanceled(FriendRequestCanceledEvent event) { plugin.onFriendRequestCanceled(event); }
		@Override public void onFriendRequestReceived(FriendRequestReceivedEvent event) { plugin.onFriendRequestReceived(event); }
		@Override public void onFriendRequestIgnored(FriendRequestIgnoredEvent event) { plugin.onFriendRequestIgnored(event); }

		//Group Events
		@Override public void onGroupJoin(GroupJoinEvent event) { plugin.onGroupJoin(event); }
		@Override public void onGroupLeave(GroupLeaveEvent event) { plugin.onGroupLeave(event); }
		@Override public void onGroupUserJoin(GroupUserJoinEvent event) { plugin.onGroupUserJoin(event); }
		@Override public void onGroupUserLeave(GroupUserLeaveEvent event) { plugin.onGroupUserLeave(event); }

		//Group Message Events
		@Override public void onGroupMessageReceived(GroupMessageReceivedEvent event) { plugin.onGroupMessageReceived(event); }
		@Override public void onGroupMessageUpdate(GroupMessageUpdateEvent event) { plugin.onGroupMessageUpdate(event); }
		@Override public void onGroupMessageDelete(GroupMessageDeleteEvent event) { plugin.onGroupMessageDelete(event); }
		@Override public void onGroupMessageEmbed(GroupMessageEmbedEvent event) { plugin.onGroupMessageEmbed(event); }

		//Group Update Events
		@Override public void onGroupUpdateIcon(GroupUpdateIconEvent event) { plugin.onGroupUpdateIcon(event); }
		@Override public void onGroupUpdateName(GroupUpdateNameEvent event){}
		@Override public void onGroupUpdateOwner(GroupUpdateOwnerEvent event) { plugin.onGroupUpdateOwner(event); }

		//Call Events
		@Override public void onCallCreate(CallCreateEvent event) { plugin.onCallCreate(event); }
		@Override public void onCallDelete(CallDeleteEvent event) { plugin.onCallDelete(event); }

		//Call Update Events
		@Override public void onCallUpdateRegion(CallUpdateRegionEvent event) { plugin.onCallUpdateRegion(event); }
		@Override public void onCallUpdateRingingUsers(CallUpdateRingingUsersEvent event) { plugin.onCallUpdateRingingUsers(event); }

		//Call Voice Events
		@Override public void onCallVoiceJoin(CallVoiceJoinEvent event) { plugin.onCallVoiceJoin(event); }
		@Override public void onCallVoiceLeave(CallVoiceLeaveEvent event) { plugin.onCallVoiceLeave(event); }
		@Override public void onCallVoiceSelfMute(CallVoiceSelfMuteEvent event) { plugin.onCallVoiceSelfMute(event); }
		@Override public void onCallVoiceSelfDeafen(CallVoiceSelfDeafenEvent event) { plugin.onCallVoiceSelfDeafen(event); }

		//Client Only Generic Events
		@Override public void onGenericRelationship(GenericRelationshipEvent event) { plugin.onGenericRelationship(event); }
		@Override public void onGenericRelationshipAdd(GenericRelationshipAddEvent event) { plugin.onGenericRelationshipAdd(event); }
		@Override public void onGenericRelationshipRemove(GenericRelationshipRemoveEvent event) { plugin.onGenericRelationshipRemove(event); }
		@Override public void onGenericGroup(GenericGroupEvent event) { plugin.onGenericGroup(event); }
		@Override public void onGenericGroupMessage(GenericGroupMessageEvent event) { plugin.onGenericGroupMessage(event); }
		@Override public void onGenericGroupUpdate(GenericGroupUpdateEvent event) { plugin.onGenericGroupUpdate(event); }
		@Override public void onGenericCall(GenericCallEvent event) { plugin.onGenericCall(event); }
		@Override public void onGenericCallUpdate(GenericCallUpdateEvent event) { plugin.onGenericCallUpdate(event); }
		@Override public void onGenericCallVoice(GenericCallVoiceEvent event) { plugin.onGenericCallVoice(event); }
	}
}