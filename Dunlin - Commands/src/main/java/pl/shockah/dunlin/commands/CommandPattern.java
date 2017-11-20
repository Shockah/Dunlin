package pl.shockah.dunlin.commands;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CommandPattern<T extends Command<?, ?>> {
	public abstract boolean matches(@Nonnull CommandContext context);
	
	@Nullable public abstract CommandPatternMatch<T> getCommand(@Nonnull CommandContext context);
}