package pl.shockah.dunlin.settings.commands;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.settings.Setting;

public class GetCommand extends NamedCommand<GetCommand.Input, Object> {
	public GetCommand() {
		super("get");
	}
	
	@Override
	public CommandResult<Input> parseInput(Message message, String textInput) {
		Scope scope = Scope.Server;
		Setting<?> setting = null;
		
		//TODO: actual implementation
		return new ParseCommandResultImpl<>(this, new Input(scope, setting));
	}
	
	@Override
	public CommandResult<Object> execute(Message message, Input input) {
		return new ValueCommandResultImpl<>(this, input.setting.get(input.scope, message.getTextChannel()));
	}

	public static final class Input {
		public final Scope scope;
		public final Setting<?> setting;
		
		public Input(Scope scope, Setting<?> setting) {
			this.scope = scope;
			this.setting = setting;
		}
	}
}