package pl.shockah.dunlin.factoids;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.*;
import pl.shockah.dunlin.factoids.db.Factoid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InfoSubcommand extends NamedCommand<InfoSubcommand.Input, Factoid> {
	@Nonnull public final FactoidsPlugin plugin;

	public InfoSubcommand(@Nonnull FactoidsPlugin plugin) {
		super("info", "i");
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
		Factoid factoid = plugin.getMatchingFactoid(input.scope, input.name);
		return new ValueCommandResult<>(this, factoid);
	}

	@Override
	@Nullable public Message formatOutput(@Nonnull CommandContext context, @Nullable Input input, @Nullable Factoid factoid) {
		if (input == null)
			throw new IllegalArgumentException();
		if (factoid == null) {
			return new MessageBuilder().setEmbed(new EmbedBuilder()
					.setColor(ErrorCommandResult.EMBED_COLOR)
					.setDescription(String.format("No factoid `%s` found in scope `%s`.", input.name, input.scope.getName()))
					.build()).build();
		}

		User author = factoid.getAuthor(plugin.manager.app.getShardManager());
		Member member = context.message.getGuild().getMember(author);
		String authorName = member == null ? author.getName() : member.getEffectiveName();

		String content = factoid.getContent();
		FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory = plugin.getFactoidCommandProvider().factories.get(factoid.getType());
		if (factory.codeHighlighting != null)
			content = String.format("```%s\n%s\n```", factory.codeHighlighting, content);

		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle(String.format("`%s` in scope `%s`", factoid.getName(), factoid.getScope(plugin.manager.app.getShardManager()).getName()), null);
		embedBuilder.setAuthor(authorName, null, author.getEffectiveAvatarUrl());
		embedBuilder.setTimestamp(factoid.getDate().toInstant());
		embedBuilder.addField("Type", factoid.getType(), true);
		embedBuilder.setDescription(content);
		return new MessageBuilder().setEmbed(embedBuilder.build()).build();
	}

	public static final class Arguments extends ArgumentSet {
		@Argument
		public Scope scope = Scope.Channel;

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