package pl.shockah.dunlin.settings.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;
import pl.shockah.dunlin.commands.result.ValueCommandResult;
import pl.shockah.dunlin.commands.result.ValueParseResult;
import pl.shockah.dunlin.settings.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GetCommand extends NamedCommand<GetCommand.Input, GetCommand.Output> {
	@Nonnull private final SettingsPlugin settingsPlugin;
	
	public GetCommand(@Nonnull SettingsPlugin settingsPlugin) {
		super("get");
		this.settingsPlugin = settingsPlugin;
	}
	
	@Override
	@Nonnull public ParseResult<Input> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
		return new ValueParseResult<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput(settingsPlugin, context.message));
	}
	
	@Override
	@Nonnull public CommandResult<Input, Output> execute(@Nonnull CommandContext context, @Nullable Input input) {
		if (input == null)
			throw new IllegalArgumentException();
		return new ValueCommandResult<>(this, input.getOutput(context.message));
	}

	@Override
	@Nullable public Message formatOutput(@Nonnull CommandContext context, @Nullable Input input, @Nullable Output output) {
		if (output == null)
			throw new IllegalArgumentException();
		return new MessageBuilder().setEmbed(new EmbedBuilder()
				.setTitle(String.format("%s in scope %s", output.setting.getFullName(), output.scope.getName()), null)
				.setDescription(String.valueOf(output.value))
		.build()).build();
	}

	public static final class Arguments extends ArgumentSet {
		@Argument
		@Nonnull public Scope scope = Scope.Channel;
		
		@Argument("setting")
		@Nonnull public String settingName;
		
		public Input toInput(@Nonnull SettingsPlugin settingsPlugin, @Nonnull Message message) {
			Setting<?> setting = settingsPlugin.getSettingByName(settingName);
			SettingScope settingScope = null;
			switch (scope) {
				case Global:
					settingScope = new GlobalSettingScope();
					break;
				case Guild:
					settingScope = new GuildSettingScope(message.getGuild());
					break;
				case Channel:
					settingScope = new TextChannelSettingScope(message.getTextChannel());
					break;
				case User:
					settingScope = new UserSettingScope(message.getAuthor());
					break;
				default:
					throw new IllegalArgumentException();
			}
			return new Input(settingScope, setting);
		}
	}

	public static final class Input {
		@Nonnull public final SettingScope scope;
		@Nonnull public final Setting<?> setting;
		
		public Input(@Nonnull SettingScope scope, @Nonnull Setting<?> setting) {
			this.scope = scope;
			this.setting = setting;
		}

		public Output getOutput(@Nonnull Message message) {
			return new Output(scope, setting, setting.get(scope));
		}
	}

	public static final class Output {
		@Nonnull public final SettingScope scope;
		@Nonnull public final Setting<?> setting;
		@Nullable public final Object value;

		public Output(@Nonnull SettingScope scope, @Nonnull Setting<?> setting, @Nullable Object value) {
			this.scope = scope;
			this.setting = setting;
			this.value = value;
		}
	}
}