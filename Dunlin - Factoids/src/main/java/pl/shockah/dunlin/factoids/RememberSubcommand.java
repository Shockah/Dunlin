package pl.shockah.dunlin.factoids;

import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;
import pl.shockah.dunlin.commands.result.ValueCommandResult;
import pl.shockah.dunlin.commands.result.ValueParseResult;
import pl.shockah.dunlin.factoids.db.Factoid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RememberSubcommand extends NamedCommand<RememberSubcommand.Input, Factoid> {
	@Nonnull public final FactoidsPlugin plugin;

	public RememberSubcommand(@Nonnull FactoidsPlugin plugin) {
		super("remember", "r");
		this.plugin = plugin;
	}

	@Override
	@Nonnull public ParseResult<Input> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
		return new ValueParseResult<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput(plugin, context.message));
	}

	@Override
	@Nonnull public CommandResult<Input, Factoid> execute(@Nonnull CommandContext context, @Nullable Input input) {
		if (input == null)
			throw new IllegalArgumentException();
		Factoid factoid = input.scope.rememberFactoid(plugin, input.factory, input.name, input.content, context.message);
		return new ValueCommandResult<>(this, factoid);
	}

	@Override
	public void output(@Nonnull CommandContext context, @Nullable Input input, @Nonnull CommandResult<Input, Factoid> outputResult) {
		context.message.addReaction("\uD83D\uDC4C").queue();
	}

	public static final class Arguments extends ArgumentSet {
		@Argument
		@Nonnull public Scope scope = Scope.Guild;

		@Argument
		public String name;

		@Argument
		@Nonnull public String type = PlainFactoidCommandFactory.TYPE;

		@Argument(isDefault = true)
		public String content;

		@Nonnull public Input toInput(@Nonnull FactoidsPlugin plugin, @Nonnull Message message) {
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

			FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory = plugin.getFactoidCommandProvider().factories.get(type);

			String content = this.content.trim();
			if (factory.codeHighlighting != null) {
				if (content.startsWith("```") && content.endsWith("```")) {
					content = content.substring(0, content.length() - 3);
					String[] split = content.split("\\r?\\n|\\r");
					content = StringUtils.join(split, "\n", 1, split.length);
				}
			}

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
		@Nonnull public final FactoidScope scope;
		@Nonnull public final FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory;
		@Nonnull public final String name;
		@Nonnull public final String content;

		public Input(@Nonnull FactoidScope scope, @Nonnull FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory, @Nonnull String name, @Nonnull String content) {
			this.scope = scope;
			this.factory = factory;
			this.name = name;
			this.content = content;
		}
	}
}