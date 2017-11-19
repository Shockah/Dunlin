package pl.shockah.dunlin.settings.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.*;
import pl.shockah.dunlin.settings.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SetCommand extends NamedCommand<SetCommand.Input, Setting<?>> {
	@Nonnull private final SettingsCommandsPlugin settingsCommandsPlugin;
	
	public SetCommand(@Nonnull SettingsCommandsPlugin settingsCommandsPlugin) {
		super("set");
		this.settingsCommandsPlugin = settingsCommandsPlugin;
	}
	
	@Override
	@Nonnull public ParseResult<Input> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
		return new ValueParseResult<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput(settingsCommandsPlugin.settingsPlugin, context.message));
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull public CommandResult<Input, Setting<?>> execute(@Nonnull CommandContext context, @Nullable Input input) {
		if (input == null)
			throw new IllegalArgumentException();
		if (input.scope instanceof GlobalSettingScope) {
			if (!settingsCommandsPlugin.permissionsPlugin.hasPermission(context.message, settingsCommandsPlugin, names[0]))
				return new ErrorCommandResult<>(this, settingsCommandsPlugin.permissionsPlugin.buildMissingPermissionMessage(settingsCommandsPlugin, names[0]));
		} else if (input.scope instanceof GuildSettingScope) {
			if (!context.message.getGuild().getMember(context.message.getAuthor()).hasPermission(Permission.MANAGE_SERVER)) {
				if (!settingsCommandsPlugin.permissionsPlugin.hasPermission(context.message, settingsCommandsPlugin, names[0]))
					return new ErrorCommandResult<>(this, settingsCommandsPlugin.permissionsPlugin.buildMissingPermissionMessage(settingsCommandsPlugin, names[0]));
			}
		} else if (input.scope instanceof TextChannelSettingScope) {
			if (!context.message.getGuild().getMember(context.message.getAuthor()).hasPermission(context.message.getTextChannel(), Permission.MANAGE_CHANNEL)) {
				if (!settingsCommandsPlugin.permissionsPlugin.hasPermission(context.message, settingsCommandsPlugin, names[0]))
					return new ErrorCommandResult<>(this, settingsCommandsPlugin.permissionsPlugin.buildMissingPermissionMessage(settingsCommandsPlugin, names[0]));
			}
		}

		Setting<Object> genericSetting = (Setting<Object>)input.setting;
		genericSetting.set(input.scope, input.value);

		return new ValueCommandResult<>(this, input.setting);
	}

	@Override
	public void output(@Nonnull CommandContext context, @Nullable Input input, @Nonnull CommandResult<Input, Setting<?>> outputResult) {
		context.message.addReaction("\uD83D\uDC4C").queue();
	}

	public static final class Arguments extends ArgumentSet {
		@Argument
		@Nonnull public Scope scope = Scope.Guild;
		
		@Argument("setting")
		public String settingName;
		
		@Argument("value")
		public String rawValue;

		@Override
		public void finalValidation() {
			//TODO: reset setting for null values instead
			if (rawValue == null)
				throw new IllegalArgumentException("Missing `value` argument.");
		}

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
			return new Input(settingScope, setting, setting.parseValue(rawValue));
		}
	}

	public static final class Input {
		@Nonnull public final SettingScope scope;
		@Nonnull public final Setting<?> setting;
		@Nullable public final Object value;
		
		public Input(@Nonnull SettingScope scope, @Nonnull Setting<?> setting, @Nullable Object value) {
			this.scope = scope;
			this.setting = setting;
			this.value = value;
		}
	}
}