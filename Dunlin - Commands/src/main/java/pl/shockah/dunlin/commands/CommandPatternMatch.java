package pl.shockah.dunlin.commands;

public final class CommandPatternMatch<T extends Command<?, ?>> {
	public final T command;
	public final String textInput;
	
	public CommandPatternMatch(T command, String textInput) {
		this.command = command;
		this.textInput = textInput;
	}
}