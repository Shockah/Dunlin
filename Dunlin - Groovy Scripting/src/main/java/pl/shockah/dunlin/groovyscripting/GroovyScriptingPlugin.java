package pl.shockah.dunlin.groovyscripting;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.transform.TimedInterrupt;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.permissions.PermissionsPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.json.JSONObject;
import pl.shockah.plugin.PluginInfo;
import pl.shockah.util.func.Func1;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class GroovyScriptingPlugin extends Plugin {
	public static final int DEFAULT_TIMEOUT = 30;

	@Dependency
	public CommandsPlugin commandsPlugin;

	@Dependency
	public PermissionsPlugin permissionsPlugin;

	private EvalCommand evalCommand;
	
	public GroovyScriptingPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info) {
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

	@Nonnull private Func1<String, Object> getEvalFunction(@Nonnull Binding binding, @Nullable GroovyInterceptor sandbox) {
		return input -> getShell(binding, sandbox).evaluate(input);
	}

	@Nullable public GroovyInterceptor getSandbox(@Nonnull User user) {
		if (user.getJDA().getAccountType() == AccountType.CLIENT && user.equals(user.getJDA().getSelfUser()))
			return null;
		if (permissionsPlugin.hasPermission(user, this, "unrestricted"))
			return null;
		return new DunlinGroovySandboxFilter();
	}

	public void injectVariables(@Nonnull Binding binding, @Nonnull Message message) {
		binding.setVariable("user", message.getAuthor());
		binding.setVariable("channel", message.getTextChannel());

		if (message.getChannelType() == ChannelType.TEXT) {
			binding.setVariable("guild", message.getGuild());
			binding.setVariable("member", message.getGuild().getMember(message.getAuthor()));
		}
	}

	@Nonnull public GroovyShell getShell(@Nonnull User user) {
		return getShell(getSandbox(user));
	}

	@Nonnull public GroovyShell getShell(@Nonnull Map<String, Object> variables, @Nonnull User user) {
		return getShell(variables, getSandbox(user));
	}

	@Nonnull public GroovyShell getShell(@Nullable GroovyInterceptor sandbox) {
		return getShell(new Binding(), sandbox);
	}

	@Nonnull public GroovyShell getShell(@Nonnull Map<String, Object> variables, @Nullable GroovyInterceptor sandbox) {
		return getShell(new Binding(variables), sandbox);
	}

	@Nonnull public GroovyShell getShell(@Nonnull Binding binding, @Nonnull User user) {
		return getShell(binding, getSandbox(user));
	}

	@Nonnull private GroovyShell getShell(@Nonnull Binding binding, @Nullable GroovyInterceptor sandbox) {
		binding.setVariable("eval", getEvalFunction(binding, sandbox));

		CompilerConfiguration cc = new CompilerConfiguration();
		cc.addCompilationCustomizers(
				new ASTTransformationCustomizer(JSONObject.of("value", getConfig().getInt("timeout", DEFAULT_TIMEOUT)), TimedInterrupt.class),
				new ImportCustomizer()
						.addStarImports("java.lang.reflect")
						.addStarImports(
								"net.dv8tion.jda.core",
								"net.dv8tion.jda.core.audio",
								"net.dv8tion.jda.core.audio.factory",
								"net.dv8tion.jda.core.audio.hooks",
								"net.dv8tion.jda.core.entities",
								"net.dv8tion.jda.core.entities.impl",
								"net.dv8tion.jda.core.events",
								"net.dv8tion.jda.core.events.channel.priv",
								"net.dv8tion.jda.core.events.channel.text",
								"net.dv8tion.jda.core.events.channel.text.update",
								"net.dv8tion.jda.core.events.channel.voice",
								"net.dv8tion.jda.core.events.channel.voice.update",
								"net.dv8tion.jda.core.events.guild",
								"net.dv8tion.jda.core.events.guild.member",
								"net.dv8tion.jda.core.events.guild.update",
								"net.dv8tion.jda.core.events.guild.voice",
								"net.dv8tion.jda.core.events.message",
								"net.dv8tion.jda.core.events.message.guild",
								"net.dv8tion.jda.core.events.message.priv",
								"net.dv8tion.jda.core.events.message.react",
								"net.dv8tion.jda.core.events.role",
								"net.dv8tion.jda.core.events.role.update",
								"net.dv8tion.jda.core.events.self",
								"net.dv8tion.jda.core.events.user",
								"net.dv8tion.jda.core.exceptions",
								"net.dv8tion.jda.core.handle",
								"net.dv8tion.jda.core.hooks",
								"net.dv8tion.jda.core.managers",
								"net.dv8tion.jda.core.managers.fields",
								"net.dv8tion.jda.core.managers.impl",
								"net.dv8tion.jda.core.requests",
								"net.dv8tion.jda.core.requests.ratelimit",
								"net.dv8tion.jda.core.requests.restaction",
								"net.dv8tion.jda.core.utils"
						)
						.addStarImports(
								"net.dv8tion.jda.bot",
								"net.dv8tion.jda.bot.entities.impl"
						)
						.addStarImports(
								"net.dv8tion.jda.client",
								"net.dv8tion.jda.client.entities",
								"net.dv8tion.jda.client.entities.impl",
								"net.dv8tion.jda.client.events.call",
								"net.dv8tion.jda.client.events.call.update",
								"net.dv8tion.jda.client.events.call.voice",
								"net.dv8tion.jda.client.events.group",
								"net.dv8tion.jda.client.events.group.update",
								"net.dv8tion.jda.client.events.message.group",
								"net.dv8tion.jda.client.events.relationship",
								"net.dv8tion.jda.client.exceptions",
								"net.dv8tion.jda.client.handle",
								"net.dv8tion.jda.client.managers",
								"net.dv8tion.jda.client.managers.fields"
						)
						.addStaticStars(Math.class.getName())
		);

		if (sandbox != null)
			cc.addCompilationCustomizers(new SandboxTransformer());

		GroovyShell shell = new GroovyShell(manager.pluginClassLoader, binding, cc);
		if (sandbox != null)
			sandbox.register();
		return shell;
	}
}