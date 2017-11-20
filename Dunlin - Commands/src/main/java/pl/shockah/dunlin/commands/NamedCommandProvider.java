package pl.shockah.dunlin.commands;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class NamedCommandProvider<Input, Output> {
	@Nullable public abstract NamedCommand<Input, Output> provide(@Nonnull CommandContext context, @Nonnull String name);
}