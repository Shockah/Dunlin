package io.shockah.dunlin.commands;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionCommandResult<T> extends ErrorCommandResult<T> {
	public final Exception exception;
	
	public ExceptionCommandResult(Exception exception) {
		super(getTitleFromException(exception), String.format("```\n%s\n```", getDescriptionFromException(exception)));
		this.exception = exception;
	}
	
	private static String getTitleFromException(Exception exception) {
		return ExceptionUtils.getStackTrace(exception).split("\n")[0];
	}
	
	private static String getDescriptionFromException(Exception exception) {
		String[] lines = ExceptionUtils.getStackTrace(exception).split("\n");
		return StringUtils.join(lines, "\n", 1, Math.min(lines.length, 6));
	}
}