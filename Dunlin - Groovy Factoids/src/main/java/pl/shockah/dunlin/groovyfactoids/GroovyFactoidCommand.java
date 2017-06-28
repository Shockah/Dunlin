package pl.shockah.dunlin.groovyfactoids;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import net.dv8tion.jda.core.entities.Message;
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
	public ParseResult<Object> parseInput(Message message, String textInput) {
		return new ValueParseResult<>(this, textInput);
	}

	@Override
	public CommandResult<Object> execute(Message message, Object o) {
		try {
			Binding binding = new Binding();
			scriptingPlugin.injectVariables(binding, message);
			return new ValueCommandResult<>(this, scriptingPlugin.getShell(binding, message.getAuthor()).evaluate(factoid.getContent()));
		} catch (GroovyRuntimeException e) {
			return new ErrorCommandResult<>(this, ErrorCommandResult.messageFromThrowable(e));
		}
	}
}