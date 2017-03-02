package pl.shockah.dunlin.music.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import pl.shockah.dunlin.music.GuildAudioManager;
import pl.shockah.util.ReadWriteList;

import java.util.ArrayList;

public abstract class Playlist {
	public final GuildAudioManager manager;
	public final ReadWriteList<AudioTrack> tracks = new ReadWriteList<>(new ArrayList<>());

	public Playlist(GuildAudioManager manager) {
		this.manager = manager;
	}

	public void queue(AudioItem item) {
		if (item instanceof AudioTrack) {
			tracks.add((AudioTrack) item);
			onQueue(item);
		} else if (item instanceof AudioPlaylist) {
			AudioPlaylist playlist = (AudioPlaylist)item;
			tracks.addAll(playlist.getTracks());
			onQueue(item);
		}
	}

	public void dequeue(AudioTrack track) {
		if (tracks.remove(track))
			onDequeue(track);
	}

	public void playNext() {
		tracks.writeOperation(tracks -> {
			if (tracks.isEmpty())
				return;

			if (manager.audioPlayer.getPlayingTrack() != null) {
				tracks.remove(0);
				if (tracks.isEmpty()) {
					manager.audioPlayer.stopTrack();
					return;
				}
			}

			manager.audioPlayer.playTrack(tracks.get(0));
		});
	}

	public abstract void onQueue(AudioItem item);

	public abstract void onDequeue(AudioTrack track);
}