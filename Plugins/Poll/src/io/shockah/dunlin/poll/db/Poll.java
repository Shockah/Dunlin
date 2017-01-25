package io.shockah.dunlin.poll.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.dunlin.db.DbObject;
import io.shockah.dunlin.poll.PollPlugin;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import pl.shockah.util.UnexpectedException;

@DatabaseTable(tableName = "io_shockah_dunlin_poll_poll")
public class Poll extends DbObject<Poll> {
	public static final String CHANNEL_COLUMN = "channel";
	public static final String MESSAGE_COLUMN = "message";
	public static final String NAME_COLUMN = "name";
	public static final String DATE_COLUMN = "date";
	public static final String MULTIPLE_CHOICE_COLUMN = "multiple_choice";
	
	@DatabaseField(canBeNull = false, columnName = CHANNEL_COLUMN)
	public String channel;
	
	@DatabaseField(canBeNull = false, columnName = MESSAGE_COLUMN)
	public String message;
	
	@DatabaseField(canBeNull = false, columnName = NAME_COLUMN)
	public String name;
	
	@DatabaseField(canBeNull = false, columnName = DATE_COLUMN)
	public Date date;
	
	@DatabaseField(columnName = MULTIPLE_CHOICE_COLUMN)
	public boolean multipleChoice;
	
	@ForeignCollectionField(foreignFieldName = "poll")
	private ForeignCollection<PollOption> options;
	
	@Deprecated //ORMLite-only
	protected Poll() {
	}
	
	public Poll(Dao<Poll, Integer> dao) {
		super(dao);
	}
	
	public ForeignCollection<PollOption> getOptions() {
		return options;
	}
	
	public void finishOrSchedule(PollPlugin plugin) {
		if (date.after(new Date())) {
			plugin.schedule(this);
			return;
		}
		
		finish(plugin.manager.app.getJDA());
	}
	
	public void finish(JDA jda) {
		try {
			TextChannel channel = jda.getTextChannelById(this.channel);
			if (channel == null) 
				throw new IllegalStateException();
			
			Message message = channel.getMessageById(this.message).block();
			if (message == null) 
				throw new IllegalStateException();
			
			channel.sendMessage(getFinishMessage(message)).queue();
			getDatabaseManager().delete(this);
		} catch (RateLimitedException e) {
			throw new UnexpectedException(e);
		}
	}
	
	private String getFinishMessage(Message message) throws RateLimitedException {
		List<User> voters = new ArrayList<>();
		PollOption[] options = getOptions().toArray(new PollOption[0]);
		List<VoteResult> voteResults = new ArrayList<>();
		List<User> multipleVoters = new ArrayList<>();
		
		for (PollOption option : options) {
			voteResults.add(new VoteResult(option));
		}
		if (voteResults.isEmpty()) {
			for (MessageReaction reaction : message.getReactions()) {
				ReactionEmote emote = reaction.getEmote();
				String emoteString = emote.isEmote() ? emote.getEmote().getAsMention() : emote.getName();
				voteResults.add(new VoteResult(emoteString, null));
			}
		}
		
		for (MessageReaction reaction : message.getReactions()) {
			for (User user : reaction.getUsers().block()) {
				if (user.isBot() || user.equals(message.getJDA().getSelfUser()))
					continue;
				ReactionEmote emote = reaction.getEmote();
				String emoteString = emote.isEmote() ? emote.getEmote().getAsMention() : emote.getName();
				
				if (!multipleChoice) {
					if (voters.contains(user)) {
						if (!multipleVoters.contains(user)) {
							multipleVoters.add(user);
							for (VoteResult result : voteResults) {
								result.voters.remove(user);
							}
						}
						continue;
					} else {
						voters.add(user);
					}
				}
				for (VoteResult result : voteResults) {
					if (Objects.equals(result.emote, emoteString)) {
						result.voters.add(user);
						break;
					}
				}
			}
		}
		Collections.sort(voteResults, (r1, r2) -> -Integer.compare(r1.voters.size(), r2.voters.size()));
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("**Poll** *%s* ended!", name));
		sb.append("\n\n**Results:**");
		
		for (VoteResult result : voteResults) {
			sb.append(String.format("\n%s: %d", result, result.voters.size()));
		}
		
		if (!multipleVoters.isEmpty()) {
			sb.append("\n\nIgnored votes (users voting multiple times):\n");
			sb.append(String.join(", ", (Iterable<String>)multipleVoters.stream().map(user -> user.getAsMention())::iterator));
		}
		
		return sb.toString();
	}
	
	private static final class VoteResult {
		public final String emote;
		public final String name;
		public final List<User> voters = new ArrayList<>();
		
		public VoteResult(String emote, String name) {
			this.emote = emote;
			this.name = name;
		}
		
		public VoteResult(PollOption option) {
			emote = option.emote;
			name = option.name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof VoteResult))
				return false;
			VoteResult result = (VoteResult)obj;
			return Objects.equals(emote, result.emote) && Objects.equals(name, result.name);
		}
		
		@Override
		public String toString() {
			return name != null ? String.format("%s %s", emote, name) : emote;
		}
	}
}