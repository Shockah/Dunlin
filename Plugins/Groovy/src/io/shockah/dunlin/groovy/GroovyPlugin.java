package io.shockah.dunlin.groovy;

import java.util.Collection;
import java.util.Map;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.collect.ImmutableMap;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.transform.TimedInterrupt;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandsPlugin;
import io.shockah.dunlin.factoids.FactoidType;
import io.shockah.dunlin.factoids.FactoidsPlugin;
import io.shockah.dunlin.plugin.Plugin;
import io.shockah.dunlin.plugin.PluginManager;
import io.shockah.skylark.func.Func1;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import pl.shockah.json.JSONList;
import pl.shockah.json.JSONObject;

public class GroovyPlugin extends Plugin {
	public static final int TIMEOUT = 30;
	
	@Dependency
	protected CommandsPlugin commandsPlugin;
	
	@Dependency("io.shockah.dunlin.factoids")
	protected Plugin factoidsOptionalPlugin;
	
	private GroovyCommand command;
	
	private Object factoidType;
	
	public GroovyPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.addNamedCommand(command = new GroovyCommand(this));
	}
	
	@Override
	protected void onAllPluginsLoaded() {
		if (factoidsOptionalPlugin != null) {
			FactoidsPlugin factoidsPlugin = (FactoidsPlugin)factoidsOptionalPlugin;
			FactoidType factoidType = new GroovyFactoidType(this);
			this.factoidType = factoidType;
			factoidsPlugin.addType(factoidType);
		}
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeNamedCommand(command);
		
		if (factoidsOptionalPlugin != null) {
			FactoidsPlugin factoidsPlugin = (FactoidsPlugin)factoidsOptionalPlugin;
			factoidsPlugin.removeType((FactoidType)factoidType);
		}
	}
	
	public GroovyShell getShell(GenericMessageEvent event) {
		return getShell(new GroovySandboxImpl(), event);
	}
	
	public GroovyShell getShell(Map<String, Object> variables, GenericMessageEvent event) {
		return getShell(variables, new GroovySandboxImpl(), event);
	}
	
	public GroovyShell getShell(GroovyInterceptor sandbox, GenericMessageEvent event) {
		return getShell(new Binding(), sandbox, event);
	}
	
	public GroovyShell getShell(Map<String, Object> variables, GroovyInterceptor sandbox, GenericMessageEvent event) {
		return getShell(new Binding(variables), sandbox, event);
	}
	
	private Func1<String, Object> getEvalFunction(Binding binding, GroovyInterceptor sandbox, GenericMessageEvent event) {
		return input -> getShell(binding, sandbox, event).evaluate(input);
	}
	
	private GroovyShell getShell(Binding binding, GroovyInterceptor sandbox, GenericMessageEvent event) {
		binding.setVariable("eval", getEvalFunction(binding, sandbox, event));
		binding.setVariable("commands", new DynamicCommandHandler(this, event));
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.addCompilationCustomizers(
				new SandboxTransformer(),
				new ASTTransformationCustomizer(ImmutableMap.of("value", TIMEOUT), TimedInterrupt.class),
				new ImportCustomizer()
					.addStarImports("java.lang.reflect")
					.addStarImports("net.dv8tion.jda")
					.addStarImports("net.dv8tion.jda.entities")
					.addStarImports("net.dv8tion.jda.managers")
					.addStaticStars(Math.class.getName())
					.addImports(HttpRequest.class.getName())
					.addImports(CommandCall.class.getName())
		);
		GroovyShell shell = new GroovyShell(manager.pluginClassLoader, binding, cc);
		sandbox.register();
		return shell;
	}
	
	@SuppressWarnings("unchecked")
	public Object turnIntoJSONValue(Object o) {
		if (o instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>)o;
			JSONObject j = new JSONObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				j.put(entry.getKey(), turnIntoJSONValue(entry.getValue()));
			}
			return j;
		} else if (o instanceof Collection<?>) {
			Collection<Object> collection = (Collection<Object>)o;
			JSONList<Object> jList = new JSONList<>();
			for (Object element : collection) {
				jList.add(turnIntoJSONValue(element));
			}
			return jList;
		} else {
			return o;
		}
	}
}