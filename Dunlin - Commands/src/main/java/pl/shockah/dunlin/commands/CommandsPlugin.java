package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pl.shockah.dunlin.commands.result.*;
import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.Setting;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.util.Box;
import pl.shockah.util.ReadWriteSet;

import java.util.LinkedHashSet;

public class CommandsPlugin extends ListenerPlugin {
	@Dependency
	private SettingsPlugin settingsPlugin;
	
	protected Setting<String> prefixesSetting;

	protected final ReadWriteSet<CommandListener> listeners = new ReadWriteSet<>(new LinkedHashSet<>());
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

	public void registerListener(CommandListener listener) {
		listeners.add(listener);
	}

	public void unregisterListener(CommandListener listener) {
		listeners.remove(listener);
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
		if (message.getJDA().getAccountType() == AccountType.CLIENT && !message.getAuthor().equals(message.getJDA().getSelfUser()))
			return;

		Box<Boolean> matchedCommand = new Box<>(false);
		patterns.iterate((pattern, iterator) -> {
			if (pattern.matches(message)) {
				CommandPatternMatch<Command<Object, Object>> commandPatternMatch = (CommandPatternMatch<Command<Object, Object>>)pattern.getCommand(message);
				if (commandPatternMatch != null) {
					matchedCommand.value = true;
					listeners.iterate(listener -> {
						listener.onCommandReceived(event, pattern, commandPatternMatch.command, commandPatternMatch.textInput);
					});
					try {
						ParseResult<Object> input = commandPatternMatch.command.parseInput(message, commandPatternMatch.textInput);
						if (input instanceof ErrorParseResult<?>) {
							respond(event, ((ErrorParseResult<?>)input).message);
						} else if (input instanceof ValueParseResult<?>) {
							ValueParseResult<?> valueParseResult = (ValueParseResult<?>)input;
							CommandResult<Object> output = commandPatternMatch.command.execute(message, valueParseResult.value);
							listeners.iterate(listener -> {
								listener.onCommandExecuted(event, pattern, commandPatternMatch.command, commandPatternMatch.textInput, output);
							});
							respond(event, output.getMessage(message, valueParseResult.value));
						}
					} catch (Exception e) {
						respond(event, ErrorCommandResult.messageFromThrowable(e));
					}
					iterator.stop();
				}
			}
		});

		if (!matchedCommand.value) {
			listeners.iterate(listener -> {
				listener.onNonCommandMessageReceived(event);
			});
		}
	}

	protected void respond(MessageReceivedEvent event, Message response) {
		if (response != null)
			event.getChannel().sendMessage(response).queue();
	}
}