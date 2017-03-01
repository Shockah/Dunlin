package pl.shockah.dunlin.music.playlist;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.music.MusicPlugin;

public class DedicatedChannelPlaylist extends PinnedMessagePlaylist {
	public final TextChannel channel;

	public DedicatedChannelPlaylist(MusicPlugin plugin, Message message) {
		super(plugin, message);
		this.channel = message.getTextChannel();
	}
}