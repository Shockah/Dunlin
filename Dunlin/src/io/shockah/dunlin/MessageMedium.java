package io.shockah.dunlin;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public abstract class MessageMedium {
	public abstract void sendMessage(String message);
	
	public static class Channel extends MessageMedium implements ChannelMessageMedium {
		public final TextChannel channel;
		
		public Channel(TextChannel channel) {
			this.channel = channel;
		}
		
		@Override
		public void sendMessage(String message) {
			channel.sendMessage(message);
		}

		@Override
		public TextChannel getChannel() {
			return channel;
		}
	}
	
	public static class Private extends MessageMedium implements PrivateMessageMedium {
		public final User user;
		
		public Private(User user) {
			this.user = user;
		}
		
		@Override
		public void sendMessage(String message) {
			user.getPrivateChannel().sendMessage(message);
		}

		@Override
		public User getUser() {
			return user;
		}
	}
	
	public interface ChannelMessageMedium {
		TextChannel getChannel();
	}
	
	public interface PrivateMessageMedium {
		User getUser();
	}
}