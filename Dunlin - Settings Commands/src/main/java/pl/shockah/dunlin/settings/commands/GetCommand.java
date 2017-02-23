package pl.shockah.dunlin.settings.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.commands.ArgumentSet;
import pl.shockah.dunlin.commands.ArgumentSetParser;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.settings.GroupSetting;
import pl.shockah.dunlin.settings.Setting;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.dunlin.settings.UserSetting;

public class GetCommand extends NamedCommand<GetCommand.Input, GetCommand.Output> {
	private final SettingsPlugin settingsPlugin;
	
	public GetCommand(SettingsPlugin settingsPlugin) {
		super("get");
		this.settingsPlugin = settingsPlugin;
	}
	
	@Override
	public CommandResult<Input> parseInput(Message message, String textInput) {
		return new ParseCommandResultImpl<>(this, new ArgumentSetParser<>(Arguments.class).parse(textInput).toInput(this));
	}
	
	@Override
	public CommandResult<Output> execute(Message message, Input input) {
		return new ValueCommandResultImpl<>(this, input.getOutput(message));
	}

	@Override
	public Message formatOutput(Message message, Input input, Output output) {
		EmbedBuilder embedBuilder = new EmbedBuilder();

		if (output.setting instanceof GroupSetting<?>)
			embedBuilder.setTitle(String.format("%s in scope %s", output.setting.getFullName(), output.scope.name()), null);
		else if (output.setting instanceof UserSetting<?>)
			embedBuilder.setTitle(String.format("%s for user %s#%s", output.setting.getFullName(), message.getAuthor().getName(), message.getAuthor().getDiscriminator()), null);
		else
			throw new IllegalArgumentException(String.format("Cannot handle setting type %s.", output.setting.getClass().getName()));

		embedBuilder.setDescription(String.valueOf(output.value));

		return new MessageBuilder().setEmbed(embedBuilder.build()).build();
	}

	public static final class Arguments extends ArgumentSet {
		@Argument
		public Scope scope = Scope.Channel;
		
		@Argument("setting")
		public String settingName;
		
		public Input toInput(GetCommand command) {
			Setting<?> setting = command.settingsPlugin.getSettingByName(settingName);
			return new Input(scope, setting);
		}
	}

	public static final class Input {
		public final Scope scope;
		public final Setting<?> setting;
		
		public Input(Scope scope, Setting<?> setting) {
			this.scope = scope;
			this.setting = setting;
		}

		public Output getOutput(Message message) {
			Object value = null;
			if (setting instanceof GroupSetting<?>)
				value = ((GroupSetting<?>)setting).get(scope, message.getTextChannel());
			else if (setting instanceof UserSetting<?>)
				value = ((UserSetting<?>)setting).get(message.getAuthor());
			else
				throw new IllegalArgumentException(String.format("Cannot handle setting type %s.", setting.getClass().getName()));
			return new Output(scope, setting, value);
		}
	}

	public static final class Output {
		public final Scope scope;
		public final Setting<?> setting;
		public final Object value;

		public Output(Scope scope, Setting<?> setting, Object value) {
			this.scope = scope;
			this.setting = setting;
			this.value = value;
		}
	}
}