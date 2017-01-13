package io.shockah.dunlin.commands;

public class CommandParseException extends Exception {
	private static final long serialVersionUID = -1260914648331453444L;
	
	public final boolean onlyMessage;
	
	public CommandParseException() {
		this(false);
	}
	
	public CommandParseException(String message) {
		this(message, false);
	}
	
	public CommandParseException(Throwable cause) {
		this(cause, false);
	}
	
	public CommandParseException(String message, Throwable cause) {
		this(message, cause, false);
	}
	
	public CommandParseException(boolean onlyMessage) {
		super();
		this.onlyMessage = onlyMessage;
	}
	
	public CommandParseException(String message, boolean onlyMessage) {
		super(message);
		this.onlyMessage = onlyMessage;
	}
	
	public CommandParseException(Throwable cause, boolean onlyMessage) {
		super(cause);
		this.onlyMessage = onlyMessage;
	}
	
	public CommandParseException(String message, Throwable cause, boolean onlyMessage) {
		super(message, cause);
		this.onlyMessage = onlyMessage;
	}
}