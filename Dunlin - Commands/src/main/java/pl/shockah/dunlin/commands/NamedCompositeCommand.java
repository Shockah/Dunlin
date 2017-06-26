package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.result.*;

public class NamedCompositeCommand extends NamedCommand<NamedCompositeCommand.Input, Object> {
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
	public ParseResult<Input> parseInput(Message message, String textInput) {
		String[] split = textInput.split("\\s+");
		Command<Object, Object> command = provider.provide(message, split[0]);
		String newTextInput = textInput.substring(split[0].length() + 1);
		if (command == null && defaultCommand != null) {
			command = (Command<Object, Object>)defaultCommand;
			newTextInput = textInput;
		}
		if (command == null)
			throw new IllegalArgumentException(String.format("Unknown subcommand: `%s`.", split[0]));
		ParseResult<Object> parseResult = command.parseInput(message, newTextInput);
		if (parseResult instanceof ErrorParseResult<?>)
			return new ErrorParseResult<>(this, ((ErrorParseResult<?>)parseResult).message);
		else
			return new ValueParseResult<>(this, new Input(command, ((ValueParseResult<Object>)parseResult).value));
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<Object> execute(Message message, Input input) {
		CommandResult<?> result = ((Command<Object, ?>)input.subcommand).execute(message, input.subcommandInput);
		if (result instanceof ErrorCommandResult<?>)
			return (ErrorCommandResult<Object>)result;
		else
			return new ValueCommandResult<>(this, new Input(input.subcommand, result));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Message formatOutput(Message message, Input input, Object output) {
		return super.formatOutput(message, input, output); //FIXME
		//return ((CommandResult<Data>)output.input).getMessage(message, output.input);
	}

	public static final class Input {
		public final Command<?, ?> subcommand;
		public final Object subcommandInput;

		public Input(Command<?, ?> subcommand, Object subcommandInput) {
			this.subcommand = subcommand;
			this.subcommandInput = subcommandInput;
		}
	}
}