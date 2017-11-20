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
import pl.shockah.jay.JSONObject;
import pl.shockah.jay.JSONPrinter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class GroovyFactoidCommand extends AbstractFactoidCommand<Object, Object> {
	@Nonnull public final GroovyScriptingPlugin scriptingPlugin;

	public GroovyFactoidCommand(@Nonnull GroovyScriptingPlugin scriptingPlugin, @Nonnull Factoid factoid, @Nonnull String name, @Nonnull String... altNames) {
		super(factoid, name, altNames);
		this.scriptingPlugin = scriptingPlugin;
	}

	@Override
	@Nonnull public ParseResult<Object> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
		return new ValueParseResult<>(this, textInput);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull public CommandResult<Object, Object> execute(@Nonnull CommandContext context, @Nullable Object o) {
		Binding binding = new Binding();
		scriptingPlugin.injectVariables(binding, context.message);
		binding.setVariable("input", o);

		JSONObject oldStore = factoid.getStoreObject(context.message.getJDA());
		if (oldStore != null)
			binding.setVariable("store", oldStore);

		try {
			Object result = scriptingPlugin.getShell(binding, context.message.getAuthor()).evaluate(factoid.getContent());
			return new ValueCommandResult<>(this, result);
		} catch (GroovyRuntimeException e) {
			return new ErrorCommandResult<>(this, ErrorCommandResult.messageFromThrowable(e));
		} finally {
			Object newStoreObject = binding.getVariable("store");
			if (newStoreObject != null) {
				if (newStoreObject instanceof Map<?, ?>) {
					JSONObject newStore = new JSONObject((Map<String, Object>)newStoreObject);
					JSONPrinter printer = new JSONPrinter();

					boolean areEqual = oldStore == newStore;
					if (oldStore != null)
						areEqual = printer.toString(oldStore).equals(printer.toString(newStore));

					if (!areEqual)
						saveFactoidStore(context, newStore);
				}
			} else if (oldStore != null) {
				saveFactoidStore(context, null);
			}
		}
	}

	private void saveFactoidStore(@Nonnull CommandContext context, @Nullable JSONObject json) {
		factoid.setStoreObject(context.message.getJDA(), json);
	}

	@Override
	@Nullable public Message formatOutput(@Nonnull CommandContext context, @Nullable Object input, @Nullable Object output) {
		if (output instanceof EmbedBuilder)
			return new MessageBuilder().setEmbed(((EmbedBuilder)output).build()).build();
		else if (output instanceof MessageEmbed)
			return new MessageBuilder().setEmbed((MessageEmbed)output).build();
		else
			return super.formatOutput(context, input, output);
	}
}