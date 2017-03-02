package pl.shockah.dunlin.music.playlist;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.music.GuildAudioManager;

public class DedicatedChannelPlaylist extends MessagePlaylist {
	public final TextChannel channel;

	public DedicatedChannelPlaylist(GuildAudioManager manager, Message message) {
		super(manager, message);
		this.channel = message.getTextChannel();
	}
}