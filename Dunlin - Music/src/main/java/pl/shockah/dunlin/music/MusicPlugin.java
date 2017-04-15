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
import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.music.playlist.DedicatedChannelPlaylist;
import pl.shockah.dunlin.music.playlist.MessagePlaylist;
import pl.shockah.dunlin.music.playlist.Playlist;
import pl.shockah.dunlin.permissions.PermissionsPlugin;
import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.EnumGroupSetting;
import pl.shockah.dunlin.settings.GroupSetting;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.util.ReadWriteMap;

import java.io.IOException;
import java.util.HashMap;

public class MusicPlugin extends ListenerPlugin {
	@Dependency
	public CommandsPlugin commandsPlugin;

	@Dependency
	public PermissionsPlugin permissionsPlugin;

	@Dependency
	public SettingsPlugin settingsPlugin;

	private QueueCommand queueCommand;
	public GroupSetting<PlaylistDisplayMode> playlistDisplayModeSetting;
	public GroupSetting<String> dedicatedChannelSetting;
	public GroupSetting<Integer> entriesPerPageSetting;

	public AudioPlayerManager audioPlayerManager;
	protected final ReadWriteMap<Guild, GuildAudioManager> guildAudioManagers = new ReadWriteMap<>(new HashMap<>());
	
	public MusicPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.registerNamedCommand(
				queueCommand = new QueueCommand(this)
		);

		settingsPlugin.register(
				playlistDisplayModeSetting = new EnumGroupSetting<>(settingsPlugin, PlaylistDisplayMode.class, this, "playlistDisplayMode", PlaylistDisplayMode.PinnedMessage)
		);
		settingsPlugin.register(
				dedicatedChannelSetting = GroupSetting.ofString(settingsPlugin, this, "dedicatedChannel", null)
		);
		settingsPlugin.register(
				entriesPerPageSetting = GroupSetting.ofInt(settingsPlugin, this, "entriesPerPage", 10)
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

		guildAudioManagers.iterateValues(guildAudioManager -> {
			try {
				guildAudioManager.close();
			} catch (IOException e) {
			}
		});
	}

	public GuildAudioManager getGuildAudioManager(Guild guild) {
		return guildAudioManagers.writeOperation(guildAudioManagers -> {
			return guildAudioManagers.computeIfAbsent(guild, k -> new GuildAudioManager(this, k));
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

	private void handleReactionChange(User user, MessageReaction reaction, MessageChannel channel) {
		if (user.isBot() || user.isFake())
			return;
		if (reaction.getEmote().isEmote())
			return;
		if (channel.getType() != ChannelType.TEXT)
			return;

		String emoji = reaction.getEmote().getName();
		if (!GuildAudioManager.EMOJIS.contains(emoji))
			return;

		TextChannel textChannel = (TextChannel)channel;
		guildAudioManagers.writeOperation(guildAudioManagers -> {
			if (!guildAudioManagers.containsKey(textChannel.getGuild()))
				return;

			GuildAudioManager guildAudioManager = getGuildAudioManager(textChannel.getGuild());
			Playlist playlist = guildAudioManager.getPlaylist(null);
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
			Playlist playlist = guildAudioManager.getPlaylist(null);
			if (playlist == null)
				return;

			if (!(playlist instanceof DedicatedChannelPlaylist))
				return;
			if (((DedicatedChannelPlaylist)playlist).channel != textChannel)
				return;
			commandsPlugin.callCommand(queueCommand, event.getMessage().getRawContent(), event);
		});
	}
}