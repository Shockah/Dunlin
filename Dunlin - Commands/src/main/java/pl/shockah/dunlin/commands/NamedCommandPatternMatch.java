package pl.shockah.dunlin.commands;

import javax.annotation.Nonnull;

public final class NamedCommandPatternMatch {
	@Nonnull public final String name;
	@Nonnull public final String input;
	
	public NamedCommandPatternMatch(@Nonnull String name, @Nonnull String input) {
		this.name = name;
		this.input = input;
	}
}