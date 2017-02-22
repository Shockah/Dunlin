package pl.shockah.dunlin.commands.result;

import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

public class ErrorCommandResultImpl<Output> extends CommandResultImpl<Output> implements ErrorCommandResult<Output> {
	public static final Color EMBED_COLOR = new Color(1f, 0.35f, 0.35f);

	protected final Message message;
	
	public ErrorCommandResultImpl(Command<?, Output> command, Message message) {
		super(command);
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