package pl.shockah.dunlin.commands;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import pl.shockah.dunlin.commands.result.*;
import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.Setting;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.pintail.PluginInfo;
import pl.shockah.util.Box;
import pl.shockah.util.ReadWriteSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;

public class CommandsPlugin extends ListenerPlugin {
	@Nonnull private final SettingsPlugin settingsPlugin;
	
	@Nonnull protected final Setting<String> prefixesSetting;

	@Nonnull protected final ReadWriteSet<CommandListener> listeners = new ReadWriteSet<>(new LinkedHashSet<>());
	@Nonnull protected final ReadWriteSet<CommandPattern<? extends Command<Object, Object>>> patterns = new ReadWriteSet<>(new LinkedHashSet<>());
	@Nonnull protected final DefaultCommandPattern defaultCommandPattern;
	@Nonnull protected final ChainCommandPattern chainCommandPattern;
	@Nonnull protected final DefaultNamedCommandProvider defaultNamedCommandProvider;
	
	public CommandsPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info, @Nonnull @RequiredDependency SettingsPlugin settingsPlugin) {
		super(manager, info);
		this.settingsPlugin = settingsPlugin;

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

	public void registerListener(@Nonnull CommandListener listener) {
		listeners.add(listener);
	}

	public void unregisterListener(@Nonnull CommandListener listener) {
		listeners.remove(listener);
	}
	
	@SuppressWarnings("unchecked")
	public void registerPattern(@Nonnull CommandPattern<? extends Command<?, ?>> pattern) {
		patterns.add((CommandPattern<? extends Command<Object, Object>>)pattern);
	}

	public void unregisterPattern(@Nonnull CommandPattern<? extends Command<?, ?>> pattern) {
		patterns.remove(pattern);
	}
	
	public void registerNamedCommandProvider(@Nonnull NamedCommandProvider<?, ?> namedCommandProvider) {
		defaultCommandPattern.registerNamedCommandProvider(namedCommandProvider);
	}
	
	public void unregisterNamedCommandProvider(@Nonnull NamedCommandProvider<?, ?> namedCommandProvider) {
		defaultCommandPattern.unregisterNamedCommandProvider(namedCommandProvider);
	}
	
	@SuppressWarnings("unchecked")
	public void registerNamedCommand(@Nonnull NamedCommand<?, ?> namedCommand) {
		defaultNamedCommandProvider.registerNamedCommand((NamedCommand<Object, Object>)namedCommand);
	}
	
	@SuppressWarnings("unchecked")
	public void unregisterNamedCommand(@Nonnull NamedCommand<?, ?> namedCommand) {
		defaultNamedCommandProvider.unregisterNamedCommand((NamedCommand<Object, Object>)namedCommand);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		CommandContext context = new CommandContext(message);
		if (message.getAuthor().isBot() || message.getAuthor().isFake())
			return;
		if (message.getJDA().getAccountType() == AccountType.CLIENT && !message.getAuthor().equals(message.getJDA().getSelfUser()))
			return;

		Box<Boolean> matchedCommand = new Box<>(false);
		patterns.iterate((pattern, iterator) -> {
			if (pattern.matches(context)) {
				CommandPatternMatch<Command<Object, Object>> commandPatternMatch = (CommandPatternMatch<Command<Object, Object>>)pattern.getCommand(context);
				if (commandPatternMatch != null) {
					matchedCommand.value = true;
					listeners.iterate(listener -> listener.onCommandReceived(context, pattern, commandPatternMatch.command, commandPatternMatch.textInput));
					callCommand(pattern, commandPatternMatch.command, commandPatternMatch.textInput, context);
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

	@SuppressWarnings("unchecked")
	public void callCommand(@Nonnull CommandPattern<?> pattern, @Nonnull Command<?, ?> command, @Nonnull String textInput, @Nonnull CommandContext context) {
		Command<Object, Object> plainCommand = (Command<Object, Object>)command;
		try {
			ParseResult<?> input = plainCommand.parseInput(context, textInput);
			if (input instanceof ErrorParseResult<?>) {
				respond(context, ((ErrorParseResult<?>)input).message);
			} else if (input instanceof ValueParseResult<?>) {
				ValueParseResult<Object> valueParseResult = (ValueParseResult<Object>)input;
				CommandResult<Object, Object> output = plainCommand.execute(context, valueParseResult.value);
				listeners.iterate(listener -> listener.onCommandExecuted(context, pattern, command, textInput, output));
				plainCommand.output(context, valueParseResult.value, output);
			}
		} catch (Exception e) {
			e.printStackTrace();
			respond(context, ErrorCommandResult.messageFromThrowable(e));
		}
	}

	protected void respond(@Nonnull CommandContext context, @Nullable Message response) {
		if (response != null)
			context.message.getChannel().sendMessage(response).queue();
	}
}