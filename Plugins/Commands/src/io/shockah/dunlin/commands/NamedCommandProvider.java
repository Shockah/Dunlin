package io.shockah.dunlin.commands;

public interface NamedCommandProvider<Input, Output> {
	NamedCommand<Input, Output> provide();
}