package io.shockah.dunlin.timedmessages.db;

import java.util.Date;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.dunlin.db.DbObject;
import io.shockah.dunlin.timedmessages.TimedMessagesPlugin;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

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
		if (this.channel != null) {
			TextChannel channel = jda.getTextChannelById(this.channel);
			if (channel != null) {
				Message message = channel.getMessageById(this.message);
				if (message != null)
					message.deleteMessage();
			}
		} else if (this.user != null) {
			User user = jda.getUserById(this.user);
			if (user != null) {
				Message message = user.getPrivateChannel().getMessageById(this.message);
				if (message != null)
					message.deleteMessage();
			}
		}
		
		getDatabaseManager().delete(this);
	}
}