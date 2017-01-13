package io.shockah.dunlin.factoids;

import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.commands.ErrorCommandResult;
import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.commands.ValueCommandResult;
import io.shockah.dunlin.factoids.ForgetCommand.Input;
import io.shockah.dunlin.factoids.db.Factoid;
import io.shockah.util.UnexpectedException;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public class ForgetCommand extends NamedCommand<Input, Factoid> {
	private final FactoidsPlugin plugin;
	
	public ForgetCommand(FactoidsPlugin plugin) {
		super("forget", "f");
		this.plugin = plugin;
	}
	
	@Override
	public Input convertToInput(GenericMessageEvent e, Object input) throws CommandParseException {
		if (input instanceof Input)
			return (Input)input;
		return super.convertToInput(e, input);
	}
	
	@Override
	public Input parseInput(GenericMessageEvent e, String input) throws CommandParseException {
		if (input.isEmpty())
			throw new CommandParseException("Not enough arguments.");
		String[] split = input.split("\\s");
		
		Factoid.Context context = plugin.getDefaultContext();
		String name = null;
		
		if (split[0].charAt(0) == '@') {
			String contextName = split[0].substring(1);
			context = Factoid.Context.valueOf(contextName);
			if (context == null)
				throw new CommandParseException(String.format("Invalid factoid context: %s", contextName));
			
			name = split[1];
		} else {
			name = split[0];
		}
		
		return new Input(context, name);
	}

	@Override
	public CommandResult<Factoid> call(CommandCall call, Input input) {
		if (input.context == Factoid.Context.Global && !plugin.hasGlobalFactoidPermission(call.event.getAuthor()))
			return new ErrorCommandResult<>("Permission required.");
		
		Factoid factoid = plugin.findActiveFactoid(call.event, input.name, input.context);
		if (factoid != null) {
			try {
				factoid.active = false;
				factoid.update();
			} catch (Exception e) {
				throw new UnexpectedException(e);
			}
		}
		
		//if (call.outputMedium == null)
		//	call.outputMedium = Medium.Notice;
		return new ValueCommandResult<>(factoid, factoid == null ? "Factoid doesn't exist." : "Forgot: " + factoid.raw);
	}
	
	public static final class Input {
		public final Factoid.Context context;
		public final String name;
		
		public Input(Factoid.Context context, String name) {
			this.context = context;
			this.name = name;
		}
	}
}