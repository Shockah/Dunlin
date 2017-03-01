package pl.shockah.dunlin.music.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.music.MusicPlugin;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class PinnedMessagePlaylist extends Playlist {
	public final MusicPlugin plugin;
	public final Message message;
	public AtomicInteger page = new AtomicInteger();

	public PinnedMessagePlaylist(MusicPlugin plugin, Message message) {
		super(plugin.audioPlayerManager.createPlayer());
		this.plugin = plugin;
		this.message = message;
	}

	@Override
	public void onQueue(AudioItem item) {
		updatePlaylistMessage();
	}

	@Override
	public void onDequeue(AudioTrack track) {
		updatePlaylistMessage();
	}

	protected void updatePlaylistMessage() {
		tracks.readOperation(tracks -> {
			int perPage = plugin.entriesPerPageSetting.get(message);
			if (!tracks.isEmpty()) {
				while (page.get() * perPage > tracks.size())
					page.decrementAndGet();
			}

			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("Playlist", null);

			String[] lines = tracks.stream()
					.map(track -> track.getInfo().title)
					.toArray(String[]::new);
			for (int i = 0; i < lines.length; i++) {
				lines[i] = String.format("%d. %s", i + 1, lines[i]);
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