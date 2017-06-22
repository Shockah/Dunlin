package pl.shockah.dunlin.factoids;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.factoids.db.Factoid;

public class ForgetSubcommand extends NamedCommand<ForgetSubcommand.Input, Factoid> {
	public final FactoidsPlugin plugin;

	public ForgetSubcommand(FactoidsPlugin plugin) {
		super("forget", "f");
		this.plugin = plugin;
	}

	@Override
	public CommandResult<Input> parseInput(Message message, String textInput) {
		return new ParseCommandResultImpl<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput(message));
	}

	@Override
	public CommandResult<Factoid> execute(Message message, Input input) {
		Factoid factoid = plugin.getFactoid(input.scope, input.name);
		if (factoid != null) {
			factoid.update(obj -> {
				obj.setForgotten(true);
			});
			message.addReaction("\uD83D\uDC4C").queue();
		}
		return new ValueCommandResultImpl<>(this, factoid);
	}

	@Override
	public Message formatOutput(Message message, Input input, Factoid factoid) {
		//TODO: `No such factoid` error message
		return null;
	}

	public static final class Arguments extends ArgumentSet {
		@Argument
		public Scope scope = Scope.Channel;

		@Argument
		public String name;

		public Input toInput(Message message) {
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

			return new Input(factoidScope, name);
		}
	}

	public static final class Input {
		public final FactoidScope scope;
		public final String name;

		public Input(FactoidScope scope, String name) {
			this.scope = scope;
			this.name = name;
		}
	}
}