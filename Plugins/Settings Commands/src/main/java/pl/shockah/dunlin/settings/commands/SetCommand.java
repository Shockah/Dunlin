package pl.shockah.dunlin.settings.commands;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.settings.Setting;

public class SetCommand extends NamedCommand<SetCommand.Input, Setting<?>> {
	public SetCommand() {
		super("set");
	}
	
	@Override
	public CommandResult<Input> parseInput(Message message, String textInput) {
		return new ParseCommandResultImpl<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<Setting<?>> execute(Message message, Input input) {
		((Setting<Object>)input.setting).set(input.value, input.scope, message.getTextChannel());
		return new ValueCommandResultImpl<>(this, input.setting);
	}
	
	public static final class Arguments extends ArgumentSet {
		@Argument
		public Scope scope = Scope.Server;
		
		@Argument("setting")
		public String settingName;
		
		@Argument("value")
		public String rawValue;
		
		public Input toInput() {
			//TODO: get actual Setting object
			Setting<?> setting = null;
			
			//TODO: parse rawValue to the proper type - should somehow standardize setting types for that
			Object value = rawValue;
			
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