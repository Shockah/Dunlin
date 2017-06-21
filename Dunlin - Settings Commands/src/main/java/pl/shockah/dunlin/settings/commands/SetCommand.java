package pl.shockah.dunlin.settings.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResultImpl;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.settings.*;

public class SetCommand extends NamedCommand<SetCommand.Input, Setting<?>> {
	private final SettingsCommandsPlugin settingsCommandsPlugin;
	
	public SetCommand(SettingsCommandsPlugin settingsCommandsPlugin) {
		super("set");
		this.settingsCommandsPlugin = settingsCommandsPlugin;
	}
	
	@Override
	public CommandResult<Input> parseInput(Message message, String textInput) {
		return new ParseCommandResultImpl<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput(settingsCommandsPlugin.settingsPlugin, message));
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<Setting<?>> execute(Message message, Input input) {
		if (input.scope instanceof GlobalSettingScope) {
			if (!settingsCommandsPlugin.permissionsPlugin.hasPermission(message, settingsCommandsPlugin, names[0]))
				return new ErrorCommandResultImpl<>(this, settingsCommandsPlugin.permissionsPlugin.buildMissingPermissionMessage(settingsCommandsPlugin, names[0]));
		} else if (input.scope instanceof GuildSettingScope) {
			if (!message.getGuild().getMember(message.getAuthor()).hasPermission(Permission.MANAGE_SERVER)) {
				if (!settingsCommandsPlugin.permissionsPlugin.hasPermission(message, settingsCommandsPlugin, names[0]))
					return new ErrorCommandResultImpl<>(this, settingsCommandsPlugin.permissionsPlugin.buildMissingPermissionMessage(settingsCommandsPlugin, names[0]));
			}
		} else if (input.scope instanceof TextChannelSettingScope) {
			if (!message.getGuild().getMember(message.getAuthor()).hasPermission(message.getTextChannel(), Permission.MANAGE_CHANNEL)) {
				if (!settingsCommandsPlugin.permissionsPlugin.hasPermission(message, settingsCommandsPlugin, names[0]))
					return new ErrorCommandResultImpl<>(this, settingsCommandsPlugin.permissionsPlugin.buildMissingPermissionMessage(settingsCommandsPlugin, names[0]));
			}
		}

		Setting<Object> genericSetting = (Setting<Object>)input.setting;
		genericSetting.set(input.scope, input.value);

		message.addReaction("\uD83D\uDC4C").queue();
		return new ValueCommandResultImpl<>(this, input.setting);
	}

	@Override
	public Message formatOutput(Setting<?> setting) {
		return null;
	}

	public static final class Arguments extends ArgumentSet {
		@Argument
		public Scope scope = Scope.Guild;
		
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

		public Input toInput(SettingsPlugin settingsPlugin, Message message) {
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
		public final SettingScope scope;
		public final Setting<?> setting;
		public final Object value;
		
		public Input(SettingScope scope, Setting<?> setting, Object value) {
			this.scope = scope;
			this.setting = setting;
			this.value = value;
		}
	}
}