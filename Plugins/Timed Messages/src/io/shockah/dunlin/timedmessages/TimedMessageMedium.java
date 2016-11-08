package io.shockah.dunlin.timedmessages;

import java.util.Date;
import io.shockah.dunlin.MessageMedium;
import io.shockah.dunlin.timedmessages.db.TimedMessageEntry;
import io.shockah.skylark.func.Action1;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

public abstract class TimedMessageMedium extends MessageMedium {
	protected final TimedMessagesPlugin plugin;
	protected final long delay;
	
	public TimedMessageMedium(TimedMessagesPlugin plugin, long delay) {
		this.plugin = plugin;
		this.delay = delay;
	}
	
	public static TimedMessageMedium of(MessageMedium medium, TimedMessagesPlugin plugin, long delay) {
		if (medium instanceof ChannelMessageMedium) {
			return new Channel(plugin, delay, ((ChannelMessageMedium)medium).getChannel());
		} else if (medium instanceof PrivateMessageMedium) {
			return new Private(plugin, delay, ((PrivateMessageMedium)medium).getUser());
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public final TimedMessageEntry createEntry(Action1<TimedMessageEntry> f) {
		return plugin.manager.app.getDatabaseManager().create(TimedMessageEntry.class, obj -> {
			obj.date = new Date(new Date().getTime() + delay);
			f.call(obj);
		});
	}
	
	public static class Channel extends TimedMessageMedium implements ChannelMessageMedium {
		public final TextChannel channel;
		
		public Channel(TimedMessagesPlugin plugin, long delay, TextChannel channel) {
			super(plugin, delay);
			this.channel = channel;
		}
		
		@Override
		public void sendMessage(String message) {
			Message messageObj = channel.sendMessage(message);
			if (messageObj != null) {
				plugin.schedule(createEntry(obj -> {
					obj.channel = channel.getId();
					obj.message = messageObj.getId();
				}));
			}
		}

		@Override
		public TextChannel getChannel() {
			return channel;
		}
	}
	
	public static class Private extends TimedMessageMedium implements PrivateMessageMedium {
		public final User user;
		
		public Private(TimedMessagesPlugin plugin, long delay, User user) {
			super(plugin, delay);
			this.user = user;
		}
		
		@Override
		public void sendMessage(String message) {
			Message messageObj = user.getPrivateChannel().sendMessage(message);
			if (messageObj != null) {
				plugin.schedule(createEntry(obj -> {
					obj.user = user.getId();
					obj.message = messageObj.getId();
				}));
			}
		}

		@Override
		public User getUser() {
			return user;
		}
	}
}