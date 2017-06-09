package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;

public class NamedCompositeCommand<Output> extends NamedCommand<NamedCompositeCommand.Input, Output> {
	public final DefaultNamedCommandProvider provider = new DefaultNamedCommandProvider();

	public NamedCompositeCommand(String name, String... altNames) {
		super(name, altNames);
	}

	@SuppressWarnings("unchecked")
	public void registerSubcommand(NamedCommand<?, ?> namedCommand) {
		provider.registerNamedCommand((NamedCommand<Object, Object>)namedCommand);
	}

	@SuppressWarnings("unchecked")
	public void unregisterSubcommand(NamedCommand<?, ?> namedCommand) {
		provider.unregisterNamedCommand((NamedCommand<Object, Object>)namedCommand);
	}

	@Override
	public CommandResult<Input> parseInput(Message message, String textInput) {
		String[] split = textInput.split("\\s+");
		NamedCommand<Object, Object> command = provider.provide(split[0]);
		if (command == null)
			throw new IllegalArgumentException(String.format("Unknown subcommand: `%s`.", split[0]));
		return new ParseCommandResultImpl<>(this, new Input(command, command.parseInput(message, textInput.substring(split[0].length() + 1))));
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<Output> execute(Message message, Input input) {
		return (CommandResult<Output>)input.command.execute(message, input.input);
	}

	public static final class Input {
		public final NamedCommand<Object, Object> command;
		public final Object input;

		public Input(NamedCommand<Object, Object> command, Object input) {
			this.command = command;
			this.input = input;
		}
	}
}