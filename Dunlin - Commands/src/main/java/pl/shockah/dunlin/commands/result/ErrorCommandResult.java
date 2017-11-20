package pl.shockah.dunlin.commands.result;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.shockah.dunlin.commands.Command;
import pl.shockah.dunlin.commands.CommandContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public final class ErrorCommandResult<Input, Output> extends CommandResult<Input, Output> {
	public static final int MAX_MESSAGE_LENGTH = 2000;
	@Nonnull public static final String MESSAGE_FORMAT = "```\n%s\n```";
	@Nonnull public static final Color EMBED_COLOR = new Color(1f, 0.35f, 0.35f);

	@Nonnull public final Message message;

	@SuppressWarnings("unchecked")
	public ErrorCommandResult(@Nonnull Command<Input, ?> command, @Nonnull Message message) {
		super((Command<Input, Output>)command);
		this.message = message;
	}

	@Override
	@Nonnull public Message getMessage(@Nonnull CommandContext context, @Nullable Input input) {
		return message;
	}

	@Nonnull public static Message messageFromThrowable(@Nonnull Throwable throwable) {
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
		if (!StringUtils.isBlank(message)) {
			if (message.length() > MessageEmbed.TITLE_MAX_LENGTH)
				message = message.substring(0, MessageEmbed.TITLE_MAX_LENGTH);
			embedBuilder.setTitle(message, null);
		}

		return new MessageBuilder().setEmbed(embedBuilder.build()).build();
	}
}