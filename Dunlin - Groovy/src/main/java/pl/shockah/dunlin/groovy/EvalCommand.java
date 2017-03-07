package pl.shockah.dunlin.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResultImpl;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;

import java.lang.reflect.InvocationTargetException;

public class EvalCommand extends NamedCommand<String, Object> {
    protected final GroovyPlugin plugin;

    public EvalCommand(GroovyPlugin plugin) {
        super("groovy", "gr");
        this.plugin = plugin;
    }

    @Override
    public CommandResult<String> parseInput(Message message, String textInput) {
        textInput = textInput.trim();
        if (textInput.startsWith("```") && textInput.endsWith("```")) {
            textInput = textInput.substring(0, textInput.length() - 3);
            String[] split = textInput.split("\\r?\\n|\\r");
            textInput = StringUtils.join(split, "\n", 1, split.length);
        }
        return new ParseCommandResultImpl<>(this, textInput);
    }

    @Override
    public CommandResult<Object> execute(Message message, String input) {
        try {
            Binding binding = new Binding();
            plugin.injectVariables(binding, message);
            return new ValueCommandResultImpl<>(this, plugin.getShell(binding, message.getAuthor()).evaluate(input));
        } catch (GroovyRuntimeException e) {
            Throwable throwable = e.getCause();
            while (throwable instanceof InvocationTargetException) {
                throwable = throwable.getCause();
            }
            return new ErrorCommandResultImpl<>(this, ErrorCommandResultImpl.messageFromThrowable(throwable));
        }
    }

    @Override
    public Message formatOutput(Message message, String s, Object o) {
        return new MessageBuilder().setEmbed(new EmbedBuilder()
                .setDescription(String.valueOf(o))
                .build()).build();
    }
}