package pl.shockah.dunlin.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.music.playlist.DedicatedChannelPlaylist;
import pl.shockah.dunlin.music.playlist.MessagePlaylist;
import pl.shockah.dunlin.music.playlist.Playlist;
import pl.shockah.dunlin.permissions.PermissionsPlugin;
import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.GuildSettingScope;
import pl.shockah.dunlin.settings.Setting;
import pl.shockah.dunlin.settings.SettingListener;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.pintail.PluginInfo;
import pl.shockah.util.ReadWriteMap;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;

public class MusicPlugin extends ListenerPlugin {
	@Nonnull public final CommandsPlugin commandsPlugin;
	@Nonnull public final PermissionsPlugin permissionsPlugin;
	@Nonnull public final SettingsPlugin settingsPlugin;

	@Nonnull private final QueueCommand queueCommand;
	@Nonnull public final Setting<PlaylistDisplayMode> playlistDisplayModeSetting;
	@Nonnull public final Setting<String> dedicatedChannelSetting;
	@Nonnull public final Setting<Integer> entriesPerPageSetting;
	@Nonnull public final Setting<Integer> volumeSetting;

	@Nonnull protected final SettingListener<Integer> volumeSettingListener;

	@Nonnull public final AudioPlayerManager audioPlayerManager;
	@Nonnull protected final ReadWriteMap<Guild, GuildAudioManager> guildAudioManagers = new ReadWriteMap<>(new HashMap<>());
	
	public MusicPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info, @Nonnull @RequiredDependency CommandsPlugin commandsPlugin, @Nonnull @RequiredDependency PermissionsPlugin permissionsPlugin, @Nonnull @RequiredDependency SettingsPlugin settingsPlugin) {
		super(manager, info);
		this.commandsPlugin = commandsPlugin;
		this.permissionsPlugin = permissionsPlugin;
		this.settingsPlugin = settingsPlugin;

		commandsPlugin.registerNamedCommand(
				queueCommand = new QueueCommand(this)
		);

		settingsPlugin.register(
				playlistDisplayModeSetting = Setting.ofEnum(settingsPlugin, this, "playlistDisplayMode", PlaylistDisplayMode.PinnedMessage)
		);
		settingsPlugin.register(
				dedicatedChannelSetting = Setting.ofString(settingsPlugin, this, "dedicatedChannel", null)
		);
		settingsPlugin.register(
				entriesPerPageSetting = Setting.ofInt(settingsPlugin, this, "entriesPerPage", 10)
		);
		settingsPlugin.register(
				volumeSetting = Setting.ofInt(settingsPlugin, this, "volume", 10)
		);

		volumeSetting.registerListener(
				volumeSettingListener = (setting, scope, value) -> {
					if (!(scope instanceof GuildSettingScope))
						return;
					GuildSettingScope guildScope = (GuildSettingScope)scope;

					Integer settingValue = setting.get(guildScope);
					if (settingValue != null)
						getGuildAudioManager(guildScope.guildScope.guild).audioPlayer.setVolume(settingValue);
				}
		);

		audioPlayerManager = new DefaultAudioPlayerManager();
		audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager(false));
		audioPlayerManager.registerSourceManager(new SoundCloudAudioSourceManager());
		audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
		audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
		audioPlayerManager.registerSourceManager(new BeamAudioSourceManager());
		audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
		audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
		//AudioSourceManagers.registerRemoteSources(audioPlayerManager);
	}

	@Override
	protected void onUnload() {
		commandsPlugin.unregisterNamedCommand(queueCommand);

		settingsPlugin.unregister(playlistDisplayModeSetting);
		settingsPlugin.unregister(dedicatedChannelSetting);
		settingsPlugin.unregister(entriesPerPageSetting);
		settingsPlugin.unregister(volumeSetting);

		guildAudioManagers.iterateValues(guildAudioManager -> {
			try {
				guildAudioManager.close();
			} catch (IOException ignored) {
			}
		});
	}

	@Nonnull public GuildAudioManager getGuildAudioManager(@Nonnull Guild guild) {
		return guildAudioManagers.writeOperation(guildAudioManagers -> {
			GuildAudioManager guildAudioManager = guildAudioManagers.computeIfAbsent(guild, k -> new GuildAudioManager(this, k));
			Integer settingValue = volumeSetting.get(new GuildSettingScope(guild));
			if (settingValue != null)
				guildAudioManager.audioPlayer.setVolume(settingValue);
			return guildAudioManager;
		});
	}

	@Override
	protected void onMessageReactionAdd(MessageReactionAddEvent event) {
		handleReactionChange(event.getUser(), event.getReaction(), event.getChannel());
	}

	@Override
	protected void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		handleReactionChange(event.getUser(), event.getReaction(), event.getChannel());
	}

	private void handleReactionChange(@Nonnull User user, @Nonnull MessageReaction reaction, @Nonnull MessageChannel channel) {
		if (user.isBot() || user.isFake())
			return;
		if (reaction.getReactionEmote().isEmote())
			return;
		if (channel.getType() != ChannelType.TEXT)
			return;

		String emoji = reaction.getReactionEmote().getName();
		if (!GuildAudioManager.EMOJIS.contains(emoji))
			return;

		TextChannel textChannel = (TextChannel)channel;
		guildAudioManagers.writeOperation(guildAudioManagers -> {
			if (!guildAudioManagers.containsKey(textChannel.getGuild()))
				return;

			GuildAudioManager guildAudioManager = getGuildAudioManager(textChannel.getGuild());
			Playlist playlist = guildAudioManager.getPlaylist(new GuildSettingScope(textChannel.getGuild()));
			if (playlist == null)
				return;

			if (!(playlist instanceof MessagePlaylist))
				return;
			MessagePlaylist messagePlaylist = (MessagePlaylist)playlist;
			messagePlaylist.onReaction(textChannel.getGuild().getMember(user), emoji);
		});
	}

	@Override
	protected void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot() || event.getAuthor().isFake())
			return;
		if (event.getChannel().getType() != ChannelType.TEXT)
			return;

		TextChannel textChannel = (TextChannel)event.getChannel();
		guildAudioManagers.writeOperation(guildAudioManagers -> {
			if (!guildAudioManagers.containsKey(textChannel.getGuild()))
				return;

			GuildAudioManager guildAudioManager = getGuildAudioManager(textChannel.getGuild());
			Playlist playlist = guildAudioManager.getPlaylist(new GuildSettingScope(event.getGuild()));
			if (playlist == null)
				return;

			if (!(playlist instanceof DedicatedChannelPlaylist))
				return;
			if (((DedicatedChannelPlaylist)playlist).channel != textChannel)
				return;
			//TODO: do it properly
			commandsPlugin.callCommand(null, queueCommand, event.getMessage().getRawContent(), new CommandContext(event.getMessage()));
		});
	}
}