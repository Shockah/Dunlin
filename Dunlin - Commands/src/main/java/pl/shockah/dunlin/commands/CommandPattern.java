package pl.shockah.dunlin.commands;

public abstract class CommandPattern<T extends Command<?, ?>> {
	public abstract boolean matches(CommandContext context);
	
	public abstract CommandPatternMatch<T> getCommand(CommandContext context);
}