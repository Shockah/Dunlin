package pl.shockah.dunlin.settings.commands;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.Scope;
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
		Scope scope = Scope.Server;
		Setting<?> setting = null;
		Object value = null;
		
		//TODO: actual implementation
		return new ParseCommandResultImpl<>(this, new Input(scope, setting, value));
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<Setting<?>> execute(Message message, Input input) {
		((Setting<Object>)input.setting).set(input.value, input.scope, message.getTextChannel());
		return new ValueCommandResultImpl<>(this, input.setting);
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