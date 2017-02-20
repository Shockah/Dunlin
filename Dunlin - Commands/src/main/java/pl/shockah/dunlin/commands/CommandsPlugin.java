package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResultImpl;
import pl.shockah.dunlin.commands.result.ParseCommandResult;
import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.Setting;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.util.ReadWriteSet;

import java.util.LinkedHashSet;

public class CommandsPlugin extends ListenerPlugin {
	@Dependency
	private SettingsPlugin settingsPlugin;
	
	protected Setting<String> prefixesSetting;
	
	protected final ReadWriteSet<CommandPattern<? extends Command<Object, Object>>> patterns = new ReadWriteSet<>(new LinkedHashSet<>());
	protected DefaultCommandPattern defaultCommandPattern;
	protected ChainCommandPattern chainCommandPattern;
	protected DefaultNamedCommandProvider defaultNamedCommandProvider;
	
	public CommandsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		settingsPlugin.register(
			prefixesSetting = Setting.ofString(settingsPlugin, this, "prefixes", ".")
		);
		
		defaultCommandPattern = new DefaultCommandPattern(this);
		chainCommandPattern = new ChainCommandPattern(defaultCommandPattern);
		registerPattern(chainCommandPattern);
		registerPattern(defaultCommandPattern);
		
		defaultNamedCommandProvider = new DefaultNamedCommandProvider();
		registerNamedCommandProvider(defaultNamedCommandProvider);
	}
	
	@Override
	protected void onUnload() {
		settingsPlugin.unregister(prefixesSetting);
	}
	
	@SuppressWarnings("unchecked")
	public void registerPattern(CommandPattern<? extends Command<?, ?>> pattern) {
		patterns.add((CommandPattern<? extends Command<Object, Object>>)pattern);
	}
	
	public void unregisterPattern(CommandPattern<? extends Command<?, ?>> pattern) {
		patterns.remove(pattern);
	}
	
	public void registerNamedCommandProvider(NamedCommandProvider<?, ?> namedCommandProvider) {
		defaultCommandPattern.registerNamedCommandProvider(namedCommandProvider);
	}
	
	public void unregisterNamedCommandProvider(NamedCommandProvider<?, ?> namedCommandProvider) {
		defaultCommandPattern.unregisterNamedCommandProvider(namedCommandProvider);
	}
	
	@SuppressWarnings("unchecked")
	public void registerNamedCommand(NamedCommand<?, ?> namedCommand) {
		defaultNamedCommandProvider.registerNamedCommand((NamedCommand<Object, Object>)namedCommand);
	}
	
	@SuppressWarnings("unchecked")
	public void unregisterNamedCommand(NamedCommand<?, ?> namedCommand) {
		defaultNamedCommandProvider.unregisterNamedCommand((NamedCommand<Object, Object>)namedCommand);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		if (message.getAuthor().isBot() || message.getAuthor().isFake())
			return;

		patterns.iterate((pattern, iterator) -> {
			if (pattern.matches(message)) {
				CommandPatternMatch<Command<Object, Object>> commandPatternMatch = (CommandPatternMatch<Command<Object, Object>>)pattern.getCommand(message);
				if (commandPatternMatch != null) {
					try {
						CommandResult<Object> input = commandPatternMatch.command.parseInput(message, commandPatternMatch.textInput);
						if (input instanceof ErrorCommandResult<?>) {
							respond(event, input.getMessage());
						} else if (input instanceof ParseCommandResult<?>) {
							CommandResult<Object> output = commandPatternMatch.command.execute(message, ((ParseCommandResult<?>)input).get());
							respond(event, output.getMessage());
						}
					} catch (Exception e) {
						respond(event, ErrorCommandResultImpl.messageFromException(e));
					}
					iterator.stop();
				}
			}
		});
	}

	protected void respond(MessageReceivedEvent event, Message response) {
		if (response != null)
			event.getChannel().sendMessage(response).queue();
	}
}