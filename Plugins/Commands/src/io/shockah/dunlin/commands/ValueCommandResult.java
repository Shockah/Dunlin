package io.shockah.dunlin.commands;

import org.apache.commons.lang3.StringUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class ValueCommandResult<T> extends CommandResult<T> {
	public final T value;
	public final String text;
	
	public ValueCommandResult(T value) {
		this(value, null);
	}
	
	public ValueCommandResult(T value, String text) {
		this.value = value;
		this.text = text;
	}
	
	@Override
	public Message getMessage() {
		String output = text;
		if (output == null && value != null)
			output = value.toString();
		if (StringUtils.isEmpty(output))
			return null;
		
		MessageBuilder builder = new MessageBuilder();
		builder.append(output);
		return builder.build();
	}
}