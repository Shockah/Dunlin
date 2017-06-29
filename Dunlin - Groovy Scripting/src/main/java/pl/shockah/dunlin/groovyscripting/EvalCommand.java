package pl.shockah.dunlin.groovyscripting;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.*;

public class EvalCommand extends NamedCommand<String, Object> {
    protected final GroovyScriptingPlugin plugin;

    public EvalCommand(GroovyScriptingPlugin plugin) {
        super("groovy", "gr");
        this.plugin = plugin;
    }

    @Override
    public ParseResult<String> parseInput(CommandContext context, String textInput) {
        textInput = textInput.trim();
        if (textInput.startsWith("```") && textInput.endsWith("```")) {
            textInput = textInput.substring(0, textInput.length() - 3);
            String[] split = textInput.split("\\r?\\n|\\r");
            textInput = StringUtils.join(split, "\n", 1, split.length);
        }
        return new ValueParseResult<>(this, textInput);
    }

    @Override
    public CommandResult<String, Object> execute(CommandContext context, String input) {
        try {
            Binding binding = new Binding();
            plugin.injectVariables(binding, context.message);
            return new ValueCommandResult<>(this, plugin.getShell(binding, context.message.getAuthor()).evaluate(input));
        } catch (GroovyRuntimeException e) {
            return new ErrorCommandResult<>(this, ErrorCommandResult.messageFromThrowable(e));
        }
    }

    @Override
    public Message formatOutput(CommandContext context, String input, Object output) {
        return new MessageBuilder().setEmbed(new EmbedBuilder()
                .setDescription(String.valueOf(output))
                .build()).build();
    }
}