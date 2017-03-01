package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

import java.awt.*;

public class ParseErrorCommandResultImpl<Output> extends CommandResultImpl<Output> implements ParseErrorCommandResult<Output> {
	public static final Color EMBED_COLOR = ErrorCommandResultImpl.EMBED_COLOR;

	protected final Message message;

	@SuppressWarnings("unchecked")
	public ParseErrorCommandResultImpl(Command<?, ?> command, Message message) {
		super((Command<?, Output>)command);
		this.message = message;
	}

	@Override
	public Message getMessage(Message message, Object input) {
		return this.message;
	}

	public static Message messageFromException(Exception e) {
		//TODO: better implementation
		String str = e.toString();
		str = str.substring(0, Math.min(2000, str.length()));
		return new MessageBuilder().setEmbed(
				new EmbedBuilder()
						.setColor(EMBED_COLOR)
						.setDescription(str)
						.build()
		).build();
	}
}