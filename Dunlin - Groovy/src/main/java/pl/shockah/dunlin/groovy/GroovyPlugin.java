package pl.shockah.dunlin.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.transform.TimedInterrupt;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.permissions.PermissionsPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.json.JSONObject;
import pl.shockah.util.func.Func1;

import java.util.Map;

public class GroovyPlugin extends Plugin {
	public static final int DEFAULT_TIMEOUT = 30;

	@Dependency
	public CommandsPlugin commandsPlugin;

	@Dependency
	public PermissionsPlugin permissionsPlugin;

	private EvalCommand evalCommand;
	
	public GroovyPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.registerNamedCommand(
				evalCommand = new EvalCommand(this)
		);
	}

	@Override
	protected void onUnload() {
		commandsPlugin.unregisterNamedCommand(evalCommand);
	}

	private Func1<String, Object> getEvalFunction(Binding binding, GroovyInterceptor sandbox) {
		return input -> getShell(binding, sandbox).evaluate(input);
	}

	public GroovyInterceptor getSandbox(User user) {
		if (permissionsPlugin.hasPermission(user, this, "unrestricted"))
			return null;
		throw new UnsupportedOperationException();
	}

	public void injectVariables(Binding binding, Message message) {
		binding.setVariable("user", message.getAuthor());
		binding.setVariable("channel", message.getTextChannel());

		if (message.getChannelType() == ChannelType.TEXT) {
			binding.setVariable("guild", message.getGuild());
			binding.setVariable("member", message.getGuild().getMember(message.getAuthor()));
		}
	}

	public GroovyShell getShell(User user) {
		return getShell(getSandbox(user));
	}

	public GroovyShell getShell(Map<String, Object> variables, User user) {
		return getShell(variables, getSandbox(user));
	}

	public GroovyShell getShell(GroovyInterceptor sandbox) {
		return getShell(new Binding(), sandbox);
	}

	public GroovyShell getShell(Map<String, Object> variables, GroovyInterceptor sandbox) {
		return getShell(new Binding(variables), sandbox);
	}

	public GroovyShell getShell(Binding binding, User user) {
		return getShell(binding, getSandbox(user));
	}

	private GroovyShell getShell(Binding binding, GroovyInterceptor sandbox) {
		binding.setVariable("eval", getEvalFunction(binding, sandbox));

		CompilerConfiguration cc = new CompilerConfiguration();
		cc.addCompilationCustomizers(
				new ASTTransformationCustomizer(JSONObject.of("value", getConfig().getInt("timeout", DEFAULT_TIMEOUT)), TimedInterrupt.class),
				new ImportCustomizer()
						.addStarImports("java.lang.reflect")
						.addStarImports("net.dv8tion.jda")
						.addStarImports("net.dv8tion.jda.entities")
						.addStarImports("net.dv8tion.jda.managers")
						.addStaticStars(Math.class.getName())
		);

		return new GroovyShell(manager.pluginClassLoader, binding, cc);
	}
}