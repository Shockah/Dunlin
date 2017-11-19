package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.result.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NamedCompositeCommand extends NamedCommand<NamedCompositeCommand.Input, NamedCompositeCommand.Output> {
	@Nullable public final Command<?, ?> defaultCommand;
	@Nonnull public final DefaultNamedCommandProvider provider = new DefaultNamedCommandProvider();

	public NamedCompositeCommand(@Nonnull String name, @Nonnull String... altNames) {
		this(null, name, altNames);
	}

	public NamedCompositeCommand(@Nullable Command<?, ?> defaultCommand, @Nonnull String name, @Nonnull String... altNames) {
		super(name, altNames);
		this.defaultCommand = defaultCommand;
	}

	@SuppressWarnings("unchecked")
	public void registerSubcommand(@Nonnull NamedCommand<?, ?> namedCommand) {
		provider.registerNamedCommand((NamedCommand<Object, Object>)namedCommand);
	}

	@SuppressWarnings("unchecked")
	public void unregisterSubcommand(@Nonnull NamedCommand<?, ?> namedCommand) {
		provider.unregisterNamedCommand((NamedCommand<Object, Object>)namedCommand);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull public ParseResult<Input> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
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
	@Nonnull public CommandResult<Input, Output> execute(@Nonnull CommandContext context, @Nullable Input input) {
		if (input == null)
			throw new IllegalArgumentException();

		CommandResult<?, ?> result = ((Command<Object, ?>)input.subcommand).execute(context, input.value);
		if (result instanceof ErrorCommandResult<?, ?>)
			return (ErrorCommandResult<Input, Output>)result;
		else
			return new ValueCommandResult<>(this, new Output(input.subcommand, result));
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull public Message formatOutput(@Nonnull CommandContext context, @Nullable Input input, @Nullable Output output) {
		if (input == null || output == null)
			throw new IllegalArgumentException();
		return ((CommandResult<Object, Object>)output.result).getMessage(context, input.value);
	}

	public static final class Input {
		@Nonnull public final Command<?, ?> subcommand;
		@Nullable public final Object value;

		public Input(@Nonnull Command<?, ?> subcommand, @Nullable Object value) {
			this.subcommand = subcommand;
			this.value = value;
		}
	}

	public static final class Output {
		@Nonnull public final Command<?, ?> subcommand;
		@Nonnull public final CommandResult<?, ?> result;

		public Output(@Nonnull Command<?, ?> subcommand, @Nonnull CommandResult<?, ?> result) {
			this.subcommand = subcommand;
			this.result = result;
		}
	}
}