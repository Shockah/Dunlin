package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.result.*;

public class NamedCompositeCommand extends NamedCommand<NamedCompositeCommand.Input, NamedCompositeCommand.Output> {
	public final Command<?, ?> defaultCommand;
	public final DefaultNamedCommandProvider provider = new DefaultNamedCommandProvider();

	public NamedCompositeCommand(String name, String... altNames) {
		this(null, name, altNames);
	}

	public NamedCompositeCommand(Command<?, ?> defaultCommand, String name, String... altNames) {
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
	public ParseResult<Input> parseInput(CommandContext context, String textInput) {
		String[] split = textInput.split("\\s+");
		Command<Object, Object> command = provider.provide(context, split[0]);
		String newTextInput = textInput.substring(split[0].length() + 1);
		if (command == null && defaultCommand != null) {
			command = (Command<Object, Object>)defaultCommand;
			newTextInput = textInput;
		}
		if (command == null)
			throw new IllegalArgumentException(String.format("Unknown subcommand: `%s`.", split[0]));
		ParseResult<Object> parseResult = command.parseInput(context, newTextInput);
		if (parseResult instanceof ErrorParseResult<?>)
			return new ErrorParseResult<>(this, ((ErrorParseResult<?>)parseResult).message);
		else
			return new ValueParseResult<>(this, new Input(command, ((ValueParseResult<Object>)parseResult).value));
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<Output> execute(CommandContext context, Input input) {
		CommandResult<?> result = ((Command<Object, ?>)input.subcommand).execute(context, input.value);
		if (result instanceof ErrorCommandResult<?>)
			return (ErrorCommandResult<Output>)result;
		else
			return new ValueCommandResult<>(this, new Output(input.subcommand, result));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Message formatOutput(CommandContext context, Input input, Output output) {
		return ((CommandResult<Object>)output.result).getMessage(context, input.value);
	}

	public static final class Input {
		public final Command<?, ?> subcommand;
		public final Object value;

		public Input(Command<?, ?> subcommand, Object value) {
			this.subcommand = subcommand;
			this.value = value;
		}
	}

	public static final class Output {
		public final Command<?, ?> subcommand;
		public final CommandResult<?> result;

		public Output(Command<?, ?> subcommand, CommandResult<?> result) {
			this.subcommand = subcommand;
			this.result = result;
		}
	}
}