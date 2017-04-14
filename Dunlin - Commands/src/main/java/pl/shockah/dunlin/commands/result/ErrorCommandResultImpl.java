package pl.shockah.dunlin.commands.result;

import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
	
	public static Message messageFromThrowable(Throwable throwable) {
		String message = ExceptionUtils.getMessage(throwable);
		String[] stackFrames = ExceptionUtils.getStackFrames(throwable);
		for (int i = 0; i < stackFrames.length; i++) {
			if (stackFrames[i].charAt(0) == '\t')
				stackFrames[i] = stackFrames[i].substring(1);
			if (stackFrames[i].startsWith("at "))
				stackFrames[i] = stackFrames[i].substring(3);
		}
		String stackTrace = StringUtils.join(stackFrames, "\n", 1, stackFrames.length);
		stackTrace = stackTrace.substring(0, Math.min(1992, stackTrace.length()));

		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(EMBED_COLOR)
				.setDescription(String.format("```\n%s\n```", stackTrace));
		if (!StringUtils.isBlank(message))
			embedBuilder.setTitle(message, null);

		return new MessageBuilder().setEmbed(embedBuilder.build()).build();
	}
}