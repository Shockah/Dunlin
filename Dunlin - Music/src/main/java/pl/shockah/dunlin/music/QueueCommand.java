package pl.shockah.dunlin.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.*;
import pl.shockah.util.Box;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class QueueCommand extends NamedCommand<AudioItem, AudioItem> {
    protected final MusicPlugin plugin;

    public QueueCommand(MusicPlugin plugin) {
        super("queue");
        this.plugin = plugin;
    }

    @Override
    public ParseResult<AudioItem> parseInput(CommandContext context, String textInput) {
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
				try {
					new URL(textInput);
					latch.countDown();
				} catch (MalformedURLException e1) {
					//TODO: add search with user choice
					try {
						String youtubeSearchQuery = String.format("ytsearch:%s", textInput);
						AudioItem audio = new YoutubeAudioSourceManager(true).loadItem((DefaultAudioPlayerManager) plugin.audioPlayerManager, new AudioReference(youtubeSearchQuery, null));
						if (audio != null) {
							if (audio instanceof AudioPlaylist)
								item.value = ((AudioPlaylist) audio).getTracks().get(0);
							else
								item.value = audio;
						}
					} catch (Exception ignored) {
					}
					latch.countDown();
				}
		    }

		    @Override
		    public void loadFailed(FriendlyException exception) {
			    latch.countDown();
		    }
	    });

	    try {
		    latch.await();
	    } catch (InterruptedException ignored) {
	    }

	    return new ValueParseResult<>(this, item.value);
    }

    @Override
    public CommandResult<AudioItem, AudioItem> execute(CommandContext context, AudioItem input) {
    	if (input == null)
			return new ValueCommandResult<>(this, null);

	    GuildAudioManager manager = plugin.getGuildAudioManager(context.message.getGuild());
	    manager.getPlaylist(context.message).queue(input);
		context.message.addReaction("\uD83D\uDC4C").queue();
        return new ValueCommandResult<>(this, input);
    }

	@Override
	public Message formatOutput(AudioItem output) {
    	if (output == null)
    		return new MessageBuilder().setEmbed(new EmbedBuilder()
				    .setColor(ErrorCommandResult.EMBED_COLOR)
				    .setDescription("Failed to load.")
				    .build()).build();

		return null;
	}
}