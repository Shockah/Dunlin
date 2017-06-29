package pl.shockah.dunlin.groovyfactoids;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.result.*;
import pl.shockah.dunlin.factoids.AbstractFactoidCommand;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.groovyscripting.GroovyScriptingPlugin;

public class GroovyFactoidCommand extends AbstractFactoidCommand<Object, Object> {
	public final GroovyScriptingPlugin scriptingPlugin;

	public GroovyFactoidCommand(GroovyScriptingPlugin scriptingPlugin, Factoid factoid, String name, String... altNames) {
		super(factoid, name, altNames);
		this.scriptingPlugin = scriptingPlugin;
	}

	@Override
	public ParseResult<Object> parseInput(CommandContext context, String textInput) {
		return new ValueParseResult<>(this, textInput);
	}

	@Override
	public CommandResult<Object, Object> execute(CommandContext context, Object o) {
		try {
			Binding binding = new Binding();
			scriptingPlugin.injectVariables(binding, context.message);
			binding.setVariable("input", o);
			return new ValueCommandResult<>(this, scriptingPlugin.getShell(binding, context.message.getAuthor()).evaluate(factoid.getContent()));
		} catch (GroovyRuntimeException e) {
			return new ErrorCommandResult<>(this, ErrorCommandResult.messageFromThrowable(e));
		}
	}

	@Override
	public Message formatOutput(CommandContext context, Object input, Object output) {
		if (output instanceof EmbedBuilder)
			return new MessageBuilder().setEmbed(((EmbedBuilder)output).build()).build();
		else if (output instanceof MessageEmbed)
			return new MessageBuilder().setEmbed((MessageEmbed)output).build();
		else
			return super.formatOutput(context, input, output);
	}
}