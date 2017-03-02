package pl.shockah.dunlin.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResultImpl;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.util.Box;

import java.util.concurrent.CountDownLatch;

public class QueueCommand extends NamedCommand<AudioItem, AudioItem> {
    protected final MusicPlugin plugin;

    public QueueCommand(MusicPlugin plugin) {
        super("queue");
        this.plugin = plugin;
    }

    @Override
    public CommandResult<AudioItem> parseInput(Message message, String textInput) {
    	Box<AudioItem> item = new Box<>();
	    CountDownLatch latch = new CountDownLatch(1);

    	plugin.audioPlayerManager.loadItem(textInput, new AudioLoadResultHandler() {
		    @Override
		    public void trackLoaded(AudioTrack track) {
		    	item.value = track;
				latch.countDown();
		    }

		    @Override
		    public void playlistLoaded(AudioPlaylist playlist) {
		    	item.value = playlist;
			    latch.countDown();
		    }

		    @Override
		    public void noMatches() {
			    latch.countDown();
		    }

		    @Override
		    public void loadFailed(FriendlyException exception) {
			    latch.countDown();
		    }
	    });

	    try {
		    latch.await();
	    } catch (InterruptedException e) {
	    }

	    return new ParseCommandResultImpl<>(this, item.value);
    }

    @Override
    public CommandResult<AudioItem> execute(Message message, AudioItem input) {
    	if (input == null)
			return new ValueCommandResultImpl<>(this, null);

	    GuildAudioManager manager = plugin.getGuildAudioManager(message.getGuild());
	    manager.getPlaylist(message).queue(input);
		message.addReaction("\uD83D\uDC4C").queue();
        return new ValueCommandResultImpl<>(this, input);
    }

	@Override
	public Message formatOutput(AudioItem output) {
    	if (output == null)
    		return new MessageBuilder().setEmbed(new EmbedBuilder()
				    .setColor(ErrorCommandResultImpl.EMBED_COLOR)
				    .setDescription("Failed to load.")
				    .build()).build();

		return null;
	}
}