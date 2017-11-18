package pl.shockah.dunlin;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;

public abstract class MessageMedium {
	public abstract void sendMessage(@Nonnull String message);
	
	public abstract void sendMessage(@Nonnull Message message);
	
	public static class Channel extends MessageMedium implements ChannelMessageMedium {
		@Nonnull public final TextChannel channel;
		
		public Channel(@Nonnull TextChannel channel) {
			this.channel = channel;
		}
		
		@Override
		public void sendMessage(@Nonnull String message) {
			channel.sendMessage(message).queue();
		}
		
		@Override
		public void sendMessage(@Nonnull Message message) {
			channel.sendMessage(message).queue();
		}

		@Override
		@Nonnull public TextChannel getChannel() {
			return channel;
		}
	}
	
	public static class Private extends MessageMedium implements PrivateMessageMedium {
		@Nonnull public final User user;
		
		public Private(@Nonnull User user) {
			this.user = user;
		}
		
		@Override
		public void sendMessage(@Nonnull String message) {
			user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
		}
		
		@Override
		public void sendMessage(@Nonnull Message message) {
			user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
		}

		@Override
		@Nonnull public User getUser() {
			return user;
		}
	}
	
	public interface ChannelMessageMedium {
		@Nonnull TextChannel getChannel();
	}
	
	public interface PrivateMessageMedium {
		@Nonnull User getUser();
	}
}