package pl.shockah.dunlin.factoids;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;
import pl.shockah.dunlin.commands.result.ValueCommandResult;
import pl.shockah.dunlin.commands.result.ValueParseResult;
import pl.shockah.dunlin.factoids.db.Factoid;

public class RememberSubcommand extends NamedCommand<RememberSubcommand.Input, Factoid> {
	public final FactoidsPlugin plugin;

	public RememberSubcommand(FactoidsPlugin plugin) {
		super("remember", "r");
		this.plugin = plugin;
	}

	@Override
	public ParseResult<Input> parseInput(Message message, String textInput) {
		return new ValueParseResult<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput(plugin, message));
	}

	@Override
	public CommandResult<Factoid> execute(Message message, Input input) {
		Factoid factoid = input.scope.rememberFactoid(plugin, input.factory, input.name, input.content, message);
		message.addReaction("\uD83D\uDC4C").queue();
		return new ValueCommandResult<>(this, factoid);
	}

	@Override
	public Message formatOutput(Message message, Input input, Factoid factoid) {
		return null;
	}

	public static final class Arguments extends ArgumentSet {
		@Argument
		public Scope scope = Scope.Channel;

		@Argument
		public String name;

		@Argument
		public String type = PlainFactoidCommandFactory.TYPE;

		@Argument
		public String content;

		public Input toInput(FactoidsPlugin plugin, Message message) {
			FactoidScope factoidScope = null;
			switch (scope) {
				case Global:
					factoidScope = new GlobalFactoidScope();
					break;
				case Guild:
					factoidScope = new GuildFactoidScope(message.getGuild());
					break;
				case Channel:
					factoidScope = new TextChannelFactoidScope(message.getTextChannel());
					break;
				default:
					throw new IllegalArgumentException();
			}

			FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory = plugin.getFactoidCommandProvider().factories.get(type);

			return new Input(factoidScope, factory, name, content);
		}

		@Override
		public void finalValidation() {
			if (name == null)
				throw new IllegalArgumentException("`name` argument is required.");
			if (content == null)
				throw new IllegalArgumentException("`content` argument is required.");
		}
	}

	public static final class Input {
		public final FactoidScope scope;
		public final FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory;
		public final String name;
		public final String content;

		public Input(FactoidScope scope, FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory, String name, String content) {
			this.scope = scope;
			this.factory = factory;
			this.name = name;
			this.content = content;
		}
	}
}