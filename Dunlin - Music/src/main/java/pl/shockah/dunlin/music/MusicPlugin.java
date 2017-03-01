package pl.shockah.dunlin.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.music.playlist.DedicatedChannelPlaylist;
import pl.shockah.dunlin.music.playlist.PinnedMessagePlaylist;
import pl.shockah.dunlin.music.playlist.Playlist;
import pl.shockah.dunlin.permissions.PermissionsPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.EnumGroupSetting;
import pl.shockah.dunlin.settings.GroupSetting;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.util.ReadWriteMap;

import java.util.HashMap;

public class MusicPlugin extends Plugin {
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
	protected final ReadWriteMap<Guild, Playlist> playlists = new ReadWriteMap<>(new HashMap<>());
	
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
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
	}

	@Override
	protected void onUnload() {
		commandsPlugin.unregisterNamedCommand(queueCommand);

		settingsPlugin.unregister(playlistDisplayModeSetting);
		settingsPlugin.unregister(dedicatedChannelSetting);
	}

	public Playlist getPlaylist(Message message) {
		return playlists.writeOperation(playlists -> {
			return playlists.computeIfAbsent(message.getGuild(), k -> createPlaylistImplementation(message));
		});
	}

	private Playlist createPlaylistImplementation(Message message) {
		switch (playlistDisplayModeSetting.get(message)) {
			case DedicatedChannel:
				return new DedicatedChannelPlaylist(this, createPlaylistMessage(getDedicatedChannel(message)));
			case PinnedMessage: {
				Message playlistMessage = createPlaylistMessage(getDedicatedChannel(message));
				playlistMessage.pin().queue();
				return new PinnedMessagePlaylist(this, playlistMessage);
			}
		}
		throw new UnsupportedOperationException();
	}

	private Message createPlaylistMessage(TextChannel channel) {
		Message message = channel.sendMessage(new EmbedBuilder().setDescription("Preparing playlist...").build()).complete();
		message.addReaction("\u23EF").queue();
		message.addReaction("\u23ED").queue();
		message.addReaction("\u2B05").queue();
		message.addReaction("\u27A1").queue();
		return message;
	}

	private TextChannel getDedicatedChannel(Message message) {
		String channelName = dedicatedChannelSetting.get(message);
		if (channelName == null)
			return message.getTextChannel();
		return message.getGuild().getTextChannelsByName(channelName, true).get(0);
	}
}