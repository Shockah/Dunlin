package pl.shockah.dunlin.settings.commands;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResultImpl;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.settings.EnumGroupSetting;
import pl.shockah.dunlin.settings.GroupSetting;
import pl.shockah.dunlin.settings.Setting;
import pl.shockah.dunlin.settings.UserSetting;

public class SetCommand extends NamedCommand<SetCommand.Input, Setting<?>> {
	private final SettingsCommandsPlugin settingsCommandsPlugin;
	
	public SetCommand(SettingsCommandsPlugin settingsCommandsPlugin) {
		super("set");
		this.settingsCommandsPlugin = settingsCommandsPlugin;
	}
	
	@Override
	public CommandResult<Input> parseInput(Message message, String textInput) {
		return new ParseCommandResultImpl<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput(this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<Setting<?>> execute(Message message, Input input) {
		if (input.scope == Scope.Global) {
			if (!settingsCommandsPlugin.permissionsPlugin.hasPermission(message, settingsCommandsPlugin, names[0]))
				return new ErrorCommandResultImpl<>(this, settingsCommandsPlugin.permissionsPlugin.buildMissingPermissionMessage(settingsCommandsPlugin, names[0]));
		} else if (input.scope == Scope.Server) {
			if (!message.getGuild().getMember(message.getAuthor()).hasPermission(Permission.MANAGE_SERVER)) {
				if (!settingsCommandsPlugin.permissionsPlugin.hasPermission(message, settingsCommandsPlugin, names[0]))
					return new ErrorCommandResultImpl<>(this, settingsCommandsPlugin.permissionsPlugin.buildMissingPermissionMessage(settingsCommandsPlugin, names[0]));
			}
		} else if (input.scope == Scope.Channel) {
			if (!message.getGuild().getMember(message.getAuthor()).hasPermission(message.getTextChannel(), Permission.MANAGE_CHANNEL)) {
				if (!settingsCommandsPlugin.permissionsPlugin.hasPermission(message, settingsCommandsPlugin, names[0]))
					return new ErrorCommandResultImpl<>(this, settingsCommandsPlugin.permissionsPlugin.buildMissingPermissionMessage(settingsCommandsPlugin, names[0]));
			}
		}

		if (input.setting instanceof GroupSetting<?>)
			((GroupSetting<Object>)input.setting).set(input.value, input.scope, message.getTextChannel());
		else if (input.setting instanceof UserSetting<?>)
			((UserSetting<Object>)input.setting).set(input.value, message.getAuthor());
		else
			throw new IllegalArgumentException(String.format("Cannot handle setting type %s.", input.setting.getClass().getName()));

		message.addReaction("\uD83D\uDC4C").queue();
		return new ValueCommandResultImpl<>(this, input.setting);
	}

	@Override
	public Message formatOutput(Setting<?> setting) {
		return null;
	}

	public static final class Arguments extends ArgumentSet {
		@Argument
		public Scope scope = Scope.Server;
		
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

		public Input toInput(SetCommand command) {
			Setting<?> setting = command.settingsCommandsPlugin.settingsPlugin.getSettingByName(settingName);
			Object value = null;
			
			switch (setting.type) {
				case Bool:
					value = ArgumentSetParser.parseBoolean(rawValue);
					break;
				case Integer:
					value = new BigInteger(rawValue);
					break;
				case Decimal:
					value = new BigDecimal(rawValue);
					break;
				case String:
					value = rawValue;
					break;
				case Enum:
					value = ArgumentSetParser.parseEnum(((EnumGroupSetting<?>)setting).enumClass, rawValue);
					break;
			}
			
			return new Input(scope, setting, value);
		}
	}

	public static final class Input {
		public final Scope scope;
		public final Setting<?> setting;
		public final Object value;
		
		public Input(Scope scope, Setting<?> setting, Object value) {
			this.scope = scope;
			this.setting = setting;
			this.value = value;
		}
	}
}