package pl.shockah.dunlin.commands;

import javax.annotation.Nonnull;

public final class CommandPatternMatch<T extends Command<?, ?>> {
	@Nonnull public final T command;
	@Nonnull public final String textInput;
	
	public CommandPatternMatch(@Nonnull T command, @Nonnull String textInput) {
		this.command = command;
		this.textInput = textInput;
	}
}