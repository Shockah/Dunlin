package pl.shockah.dunlin.commands.result;

import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.Command;

public class ErrorCommandResultImpl<Output> extends CommandResultImpl<Output> implements ErrorCommandResult<Output> {
	protected final Message message;
	
	public ErrorCommandResultImpl(Command<?, Output> command, Message message) {
		super(command);
		this.message = message;
	}

	@Override
	public Message getMessage() {
		return message;
	}
	
	public static Message messageFromException(Exception e) {
		//TODO: better implementation
		return new MessageBuilder().setEmbed(
			new EmbedBuilder()
				.setColor(Color.RED)
				.setDescription(e.toString().substring(0, 2000))
				.build()
			).build();
	}
}