package pl.shockah.dunlin.music.playlist;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.music.GuildAudioManager;

import javax.annotation.Nonnull;

public class DedicatedChannelPlaylist extends MessagePlaylist {
	@Nonnull
	public final TextChannel channel;

	public DedicatedChannelPlaylist(@Nonnull GuildAudioManager manager, @Nonnull Message message) {
		super(manager, message);
		this.channel = message.getTextChannel();
	}
}