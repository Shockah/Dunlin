package pl.shockah.dunlin.commands;

public abstract class NamedCommand<Input, Output> extends Command<Input, Output> {
	public final String[] names;
	
	public NamedCommand(String name, String... altNames) {
		String[] joinedNames = new String[altNames.length + 1];
		System.arraycopy(altNames, 0, joinedNames, 1, altNames.length);
		joinedNames[0] = name;
		names = joinedNames;
	}
}