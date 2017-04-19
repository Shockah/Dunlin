package pl.shockah.dunlin.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.*;
import pl.shockah.dunlin.db.DatabaseManager;
import pl.shockah.dunlin.music.db.MessagePlaylistEntry;
import pl.shockah.dunlin.music.playlist.DedicatedChannelPlaylist;
import pl.shockah.dunlin.music.playlist.MessagePlaylist;
import pl.shockah.dunlin.music.playlist.Playlist;
import pl.shockah.dunlin.settings.GuildSettingScope;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GuildAudioManager extends AudioEventAdapter implements AudioSendHandler, Closeable {
    public static final String EMOJI_PLAY_PAUSE = "\u23EF";
    public static final String EMOJI_NEXT_TRACK = "\u23ED";
    public static final String EMOJI_ARROW_LEFT = "\u2B05";
    public static final String EMOJI_ARROW_RIGHT = "\u27A1";
    public static final List<String> EMOJIS = Arrays.asList(EMOJI_PLAY_PAUSE, EMOJI_NEXT_TRACK, EMOJI_ARROW_LEFT, EMOJI_ARROW_RIGHT);

    public final MusicPlugin plugin;
    public final Guild guild;
    public final AudioPlayer audioPlayer;
    public final Object lock = new Object();
    protected VoiceChannel channel;
    protected AudioFrame lastFrame;
    protected Playlist playlist;

    public GuildAudioManager(MusicPlugin plugin, Guild guild) {
        this.plugin = plugin;
        this.guild = guild;
        audioPlayer = plugin.audioPlayerManager.createPlayer();
        audioPlayer.addListener(this);
    }

    @Override
    public boolean canProvide() {
        return (lastFrame = audioPlayer.provide()) != null;
    }

    @Override
    public byte[] provide20MsAudio() {
        return lastFrame.data;
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        playlist.dequeue(track);
        if (!endReason.mayStartNext)
            return;
        playlist.playNext();
    }

    public Playlist getPlaylist(Message message) {
        synchronized (lock) {
            if (playlist != null)
                return playlist;
            return (playlist = createPlaylistImplementation(message));
        }
    }

    private Playlist createPlaylistImplementation(Message message) {
        synchronized (lock) {
            PlaylistDisplayMode playlistDisplayMode = plugin.playlistDisplayModeSetting.get(new GuildSettingScope(message.getGuild()));
            switch (playlistDisplayMode) {
                case DedicatedChannel:
                case PinnedMessage: {
                    DatabaseManager db = plugin.manager.app.getDatabaseManager();
                    MessagePlaylistEntry entry = db.selectFirst(MessagePlaylistEntry.class, q -> q.where().eq(MessagePlaylistEntry.GUILD_ID, guild.getId()));
                    Message playlistMessage = null;

                    if (entry == null) {
                        if (message == null)
                            return null;

                        playlistMessage = createPlaylistMessage(getDedicatedChannel(message));
                        final Message f_playlistMessage = playlistMessage;
                        entry = db.create(MessagePlaylistEntry.class, obj -> {
                            obj.setMessage(f_playlistMessage);
                        });

                        if (playlistDisplayMode == PlaylistDisplayMode.PinnedMessage)
                            playlistMessage.pin().queue();
                    } else {
                        playlistMessage = entry.getMessage(plugin.manager.app.getShardManager()).complete();
                    }

                    switch (playlistDisplayMode) {
                        case DedicatedChannel:
                            return new DedicatedChannelPlaylist(this, playlistMessage);
                        case PinnedMessage:
                            return new MessagePlaylist(this, playlistMessage);
                    }
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    private Message createPlaylistMessage(TextChannel channel) {
        Message message = channel.sendMessage(new EmbedBuilder().setDescription("Preparing playlist...").build()).complete();
        message.addReaction(EMOJI_PLAY_PAUSE).queue();
        message.addReaction(EMOJI_NEXT_TRACK).queue();
        message.addReaction(EMOJI_ARROW_LEFT).queue();
        message.addReaction(EMOJI_ARROW_RIGHT).queue();
        return message;
    }

    private TextChannel getDedicatedChannel(Message message) {
        String channelName = plugin.dedicatedChannelSetting.get(new GuildSettingScope(message.getGuild()));
        if (channelName == null)
            return message.getTextChannel();
        return message.getGuild().getTextChannelsByName(channelName, true).get(0);
    }

    public VoiceChannel getCurrentVoiceChannel() {
        return channel;
    }

    public void openAudioConnection(VoiceChannel channel, Member member) {
        synchronized (lock) {
            if (this.channel == channel)
                return;

            if (this.channel != null)
                checkChangeVoiceChannelPermission(member);

            channel.getGuild().getAudioManager().openAudioConnection(channel);
            channel.getGuild().getAudioManager().setSendingHandler(this);
            this.channel = channel;
        }
    }

    public void closeAudioConnection() {
        synchronized (lock) {
            if (channel != null) {
                channel.getGuild().getAudioManager().closeAudioConnection();
                channel = null;
            }
        }
    }

    private void checkChangeVoiceChannelPermission(Member member) {
        if (audioPlayer.getPlayingTrack() == null)
            return;
        if (member.hasPermission(Permission.VOICE_MOVE_OTHERS))
            return;
        plugin.permissionsPlugin.checkPermission(member, plugin, "changeVoiceChannel");
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            audioPlayer.destroy();
            closeAudioConnection();
        }
    }
}