package pl.shockah.dunlin.commands;

import javax.annotation.Nonnull;

public abstract class NamedCommand<Input, Output> extends Command<Input, Output> {
	@Nonnull public final String[] names;
	
	public NamedCommand(@Nonnull String name, @Nonnull String... altNames) {
		String[] joinedNames = new String[altNames.length + 1];
		System.arraycopy(altNames, 0, joinedNames, 1, altNames.length);
		joinedNames[0] = name;
		names = joinedNames;
	}
}