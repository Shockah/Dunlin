package pl.shockah.dunlin.music.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import pl.shockah.util.ReadWriteList;

import java.util.ArrayList;

public abstract class Playlist extends AudioEventAdapter {
	public final ReadWriteList<AudioTrack> tracks = new ReadWriteList<>(new ArrayList<>());
	public final AudioPlayer audioPlayer;

	public Playlist(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
		audioPlayer.addListener(this);
	}

	public void queue(AudioItem item) {
		if (item instanceof AudioTrack) {
			tracks.add((AudioTrack) item);
			if (audioPlayer.getPlayingTrack() == null)
				playNext();
			onQueue(item);
		} else if (item instanceof AudioPlaylist) {
			AudioPlaylist playlist = (AudioPlaylist)item;
			tracks.addAll(playlist.getTracks());
			if (audioPlayer.getPlayingTrack() == null)
				playNext();
			onQueue(item);
		}
	}

	public void dequeue(AudioTrack track) {
		if (tracks.remove(track))
			onDequeue(track);
	}

	public void playNext() {
		tracks.readOperation(tracks -> {
			if (tracks.isEmpty())
				return;
			audioPlayer.playTrack(tracks.get(0));
		});
	}

	public abstract void onQueue(AudioItem item);

	public abstract void onDequeue(AudioTrack track);

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		tracks.writeOperation(tracks -> {
			if (tracks.isEmpty())
				return;
			tracks.remove(0);

			if (!endReason.mayStartNext)
				return;
			playNext();
		});
	}
}