package io.shockah.dunlin.timedmessages.db;

import java.util.Date;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.dunlin.db.DbObject;
import io.shockah.dunlin.timedmessages.TimedMessagesPlugin;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import pl.shockah.util.UnexpectedException;

@DatabaseTable(tableName = "io_shockah_dunlin_timedmessages_timedmessageentry")
public class TimedMessageEntry extends DbObject<TimedMessageEntry> {
	public static final String MESSAGE_COLUMN = "message";
	public static final String CHANNEL_COLUMN = "channel";
	public static final String USER_COLUMN = "user";
	public static final String DATE_COLUMN = "date";
	
	@DatabaseField(canBeNull = false, columnName = MESSAGE_COLUMN)
	public String message;
	
	@DatabaseField(canBeNull = true, columnName = CHANNEL_COLUMN)
	public String channel;
	
	@DatabaseField(canBeNull = true, columnName = USER_COLUMN)
	public String user;
	
	@DatabaseField(canBeNull = false, columnName = DATE_COLUMN)
	public Date date;
	
	@Deprecated //ORMLite-only
	protected TimedMessageEntry() {
	}
	
	public TimedMessageEntry(Dao<TimedMessageEntry, Integer> dao) {
		super(dao);
	}
	
	public void removeOrSchedule(TimedMessagesPlugin plugin) {
		if (date.after(new Date())) {
			plugin.schedule(this);
			return;
		}
		
		remove(plugin.manager.app.getJDA());
	}
	
	public void remove(JDA jda) {
		try {
			if (this.channel != null) {
				TextChannel channel = jda.getTextChannelById(this.channel);
				if (channel != null) {
					Message message = channel.getMessageById(this.message).block();
					if (message != null)
						message.deleteMessage();
				}
			} else if (this.user != null) {
				User user = jda.getUserById(this.user);
				if (user != null) {
					Message message = user.getPrivateChannel().getMessageById(this.message).block();
					if (message != null)
						message.deleteMessage();
				}
			}
		} catch (RateLimitedException e) {
			throw new UnexpectedException(e);
		}
		
		getDatabaseManager().delete(this);
	}
}