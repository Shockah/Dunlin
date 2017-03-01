package pl.shockah.dunlin.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {
	protected final AudioPlayer audioPlayer;
	protected AudioFrame lastFrame;

	public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
	}

	@Override
	public boolean canProvide() {
		lastFrame = audioPlayer.provide();
		return lastFrame != null;
	}

	@Override
	public byte[] provide20MsAudio() {
		return lastFrame.data;
	}

	@Override
	public boolean isOpus() {
		return true;
	}
}