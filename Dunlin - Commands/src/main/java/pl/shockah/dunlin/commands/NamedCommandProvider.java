package pl.shockah.dunlin.commands;

public abstract class NamedCommandProvider<Input, Output> {
	public abstract NamedCommand<Input, Output> provide(String name);
}