package pl.shockah.dunlin.settings.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.settings.*;

public class GetCommand extends NamedCommand<GetCommand.Input, GetCommand.Output> {
	private final SettingsPlugin settingsPlugin;
	
	public GetCommand(SettingsPlugin settingsPlugin) {
		super("get");
		this.settingsPlugin = settingsPlugin;
	}
	
	@Override
	public CommandResult<Input> parseInput(Message message, String textInput) {
		return new ParseCommandResultImpl<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput(this, message));
	}
	
	@Override
	public CommandResult<Output> execute(Message message, Input input) {
		return new ValueCommandResultImpl<>(this, input.getOutput(message));
	}

	@Override
	public Message formatOutput(Message message, Input input, Output output) {
		return new MessageBuilder().setEmbed(new EmbedBuilder()
				.setTitle(String.format("%s in scope %s", output.setting.getFullName(), output.scope.name()), null)
				.setDescription(String.valueOf(output.value))
		.build()).build();
	}

	public static final class Arguments extends ArgumentSet {
		@Argument
		public Scope scope = Scope.Channel;
		
		@Argument("setting")
		public String settingName;
		
		public Input toInput(GetCommand command, Message message) {
			Setting<?> setting = command.settingsPlugin.getSettingByName(settingName);
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
		public final SettingScope scope;
		public final Setting<?> setting;
		
		public Input(SettingScope scope, Setting<?> setting) {
			this.scope = scope;
			this.setting = setting;
		}

		public Output getOutput(Message message) {
			return new Output(scope, setting, setting.get(scope));
		}
	}

	public static final class Output {
		public final SettingScope scope;
		public final Setting<?> setting;
		public final Object value;

		public Output(SettingScope scope, Setting<?> setting, Object value) {
			this.scope = scope;
			this.setting = setting;
			this.value = value;
		}
	}
}