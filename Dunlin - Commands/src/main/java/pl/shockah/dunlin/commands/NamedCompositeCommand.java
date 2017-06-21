package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;

public class NamedCompositeCommand<Output> extends NamedCommand<NamedCompositeCommand.Input, Output> {
	public final Command<?, Output> defaultCommand;
	public final DefaultNamedCommandProvider provider = new DefaultNamedCommandProvider();

	public NamedCompositeCommand(String name, String... altNames) {
		this(null, name, altNames);
	}

	public NamedCompositeCommand(Command<?, Output> defaultCommand, String name, String... altNames) {
		super(name, altNames);
		this.defaultCommand = defaultCommand;
	}

	@SuppressWarnings("unchecked")
	public void registerSubcommand(NamedCommand<?, ?> namedCommand) {
		provider.registerNamedCommand((NamedCommand<Object, Object>)namedCommand);
	}

	@SuppressWarnings("unchecked")
	public void unregisterSubcommand(NamedCommand<?, ?> namedCommand) {
		provider.unregisterNamedCommand((NamedCommand<Object, Object>)namedCommand);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<Input> parseInput(Message message, String textInput) {
		String[] split = textInput.split("\\s+");
		Command<Object, Object> command = provider.provide(message, split[0]);
		String newTextInput = textInput.substring(split[0].length() + 1);
		if (command == null && defaultCommand != null) {
			command = (Command<Object, Object>)defaultCommand;
			newTextInput = textInput;
		}
		if (command == null)
			throw new IllegalArgumentException(String.format("Unknown subcommand: `%s`.", split[0]));
		return new ParseCommandResultImpl<>(this, new Input(command, command.parseInput(message, newTextInput)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<Output> execute(Message message, Input input) {
		return (CommandResult<Output>)input.command.execute(message, input.input);
	}

	public static final class Input {
		public final Command<Object, Object> command;
		public final Object input;

		public Input(Command<Object, Object> command, Object input) {
			this.command = command;
			this.input = input;
		}
	}
}