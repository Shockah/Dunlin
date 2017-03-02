package pl.shockah.dunlin.music.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import pl.shockah.dunlin.music.GuildAudioManager;
import pl.shockah.dunlin.music.MusicPlugin;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class MessagePlaylist extends Playlist {
	public final Message message;
	public AtomicInteger page = new AtomicInteger();

	public MessagePlaylist(GuildAudioManager manager, Message message) {
		super(manager);
		this.message = message;
	}

	@Override
	public void onQueue(AudioItem item) {
		synchronized (manager.lock) {
			if (manager.getCurrentVoiceChannel() != null && manager.audioPlayer.getPlayingTrack() == null)
				playNext();
			updatePlaylistMessage();
		}
	}

	@Override
	public void onDequeue(AudioTrack track) {
		updatePlaylistMessage();
	}

	public void onReaction(Member member, String emoji) {
		synchronized (manager.lock) {
			if (emoji.equals(GuildAudioManager.EMOJI_PLAY_PAUSE)) {
				VoiceChannel channel = manager.getCurrentVoiceChannel();
				VoiceChannel userVoiceChannel = member.getVoiceState().getChannel();

				if (channel == null || channel != userVoiceChannel) {
					manager.openAudioConnection(userVoiceChannel, member);
					if (channel == null)
						playNext();
				} else {
					manager.audioPlayer.setPaused(!manager.audioPlayer.isPaused());
					updatePlaylistMessage();
				}
			} else if (emoji.equals(GuildAudioManager.EMOJI_NEXT_TRACK)) {
				VoiceChannel channel = manager.getCurrentVoiceChannel();
				VoiceChannel userVoiceChannel = member.getVoiceState().getChannel();

				if (channel != null || channel == userVoiceChannel)
					playNext();
			} else if (emoji.equals(GuildAudioManager.EMOJI_ARROW_LEFT)) {
				page.set(Math.max(page.get() - 1, 0));
				updatePlaylistMessage();
			} else if (emoji.equals(GuildAudioManager.EMOJI_ARROW_RIGHT)) {
				int perPage = manager.plugin.entriesPerPageSetting.get(message);
				int pages = (int)Math.ceil(1.0 * tracks.size() / perPage);
				page.set(Math.min(page.get() + 1, Math.max(pages - 1, 0)));
				updatePlaylistMessage();
			}
		}
	}

	@Override
	public void playNext() {
		super.playNext();
		updatePlaylistMessage();
	}

	protected void updatePlaylistMessage() {
		tracks.readOperation(tracks -> {
			int perPage = manager.plugin.entriesPerPageSetting.get(message);
			if (!tracks.isEmpty()) {
				while (page.get() * perPage > tracks.size())
					page.decrementAndGet();
			}

			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("Playlist", null);

			String[] lines = tracks.stream()
					.map(track -> String.format("**%s** by **%s**", track.getInfo().title, track.getInfo().author))
					.toArray(String[]::new);
			for (int i = 0; i < lines.length; i++) {
				lines[i] = String.format("**%d**. %s", i + 1, lines[i]);
			}

			lines = Arrays.stream(lines).skip(page.get() * perPage).limit(perPage).toArray(String[]::new);

			embed.setDescription(String.join("\n", lines));
			int pages = (int)Math.ceil(1.0 * tracks.size() / perPage);
			if (pages > 1)
				embed.setFooter(String.format("Page %d/%d", page.get() + 1, pages), null);

			message.editMessage(embed.build()).queue();
		});
	}
}