package pl.shockah.dunlin.settings.commands;

import java.math.BigDecimal;
import java.math.BigInteger;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.settings.EnumSetting;
import pl.shockah.dunlin.settings.Setting;
import pl.shockah.dunlin.settings.SettingsPlugin;

public class SetCommand extends NamedCommand<SetCommand.Input, Setting<?>> {
	private final SettingsPlugin settingsPlugin;
	
	public SetCommand(SettingsPlugin settingsPlugin) {
		super("set");
		this.settingsPlugin = settingsPlugin;
	}
	
	@Override
	public CommandResult<Input> parseInput(Message message, String textInput) {
		return new ParseCommandResultImpl<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput(this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<Setting<?>> execute(Message message, Input input) {
		((Setting<Object>)input.setting).set(input.value, input.scope, message.getTextChannel());
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
			Setting<?> setting = command.settingsPlugin.getSettingByName(settingName);
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
					value = ArgumentSetParser.parseEnum(((EnumSetting<?>)setting).enumClass, rawValue);
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