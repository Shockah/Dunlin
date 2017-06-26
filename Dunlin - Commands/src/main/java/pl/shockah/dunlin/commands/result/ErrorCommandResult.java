package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.shockah.dunlin.commands.Command;

import java.awt.*;

public final class ErrorCommandResult<T> extends CommandResult<T> {
	public static final int MAX_MESSAGE_LENGTH = 2000;
	public static final String MESSAGE_FORMAT = "```\n%s\n```";
	public static final Color EMBED_COLOR = new Color(1f, 0.35f, 0.35f);

	public final Message message;

	public ErrorCommandResult(Command<?, T> command, Message message) {
		super(command);
		this.message = message;
	}

	@Override
	public Message getMessage(Message originalMessage, T input) {
		return message;
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
		stackTrace = stackTrace.substring(0, Math.min(MAX_MESSAGE_LENGTH - String.format(MESSAGE_FORMAT, "").length(), stackTrace.length()));

		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(EMBED_COLOR)
				.setDescription(String.format(MESSAGE_FORMAT, stackTrace));
		if (!StringUtils.isBlank(message))
			embedBuilder.setTitle(message, null);

		return new MessageBuilder().setEmbed(embedBuilder.build()).build();
	}
}