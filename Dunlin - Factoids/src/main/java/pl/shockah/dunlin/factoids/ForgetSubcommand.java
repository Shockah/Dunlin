package pl.shockah.dunlin.factoids;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.*;
import pl.shockah.dunlin.factoids.db.Factoid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ForgetSubcommand extends NamedCommand<ForgetSubcommand.Input, Factoid> {
	@Nonnull public final FactoidsPlugin plugin;

	public ForgetSubcommand(@Nonnull FactoidsPlugin plugin) {
		super("forget", "f");
		this.plugin = plugin;
	}

	@Override
	@Nonnull public ParseResult<Input> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
		return new ValueParseResult<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput(context.message));
	}

	@Override
	@Nonnull public CommandResult<Input, Factoid> execute(@Nonnull CommandContext context, @Nullable Input input) {
		if (input == null)
			throw new IllegalArgumentException();
		Factoid factoid = plugin.getFactoid(input.scope, input.name);
		if (factoid != null)
			factoid.update(obj -> {
				obj.setForgotten(true);
			});
		return new ValueCommandResult<>(this, factoid);
	}

	@Override
	public void output(@Nonnull CommandContext context, @Nullable Input input, @Nonnull CommandResult<Input, Factoid> outputResult) {
		if (outputResult instanceof ValueCommandResult<?, ?> && ((ValueCommandResult<Input, Factoid>)outputResult).value == null)
			context.message.addReaction("\uD83D\uDC4C").queue();
		else
			super.output(context, input, outputResult);
	}

	@Override
	@Nullable public Message formatOutput(@Nonnull CommandContext context, @Nullable Input input, @Nullable Factoid factoid) {
		if (input == null)
			throw new IllegalArgumentException();
		return new MessageBuilder().setEmbed(new EmbedBuilder()
				.setColor(ErrorCommandResult.EMBED_COLOR)
				.setDescription(String.format("No factoid `%s` found in scope `%s`.", input.name, input.scope.getName()))
				.build()).build();
	}

	public static final class Arguments extends ArgumentSet {
		@Argument
		@Nonnull public Scope scope = Scope.Channel;

		@Argument
		public String name;

		@Nonnull public Input toInput(@Nonnull Message message) {
			FactoidScope factoidScope;
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
		@Nonnull public final FactoidScope scope;
		@Nonnull public final String name;

		public Input(@Nonnull FactoidScope scope, @Nonnull String name) {
			this.scope = scope;
			this.name = name;
		}
	}
}