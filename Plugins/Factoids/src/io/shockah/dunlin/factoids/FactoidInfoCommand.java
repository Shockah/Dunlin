package io.shockah.dunlin.factoids;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import com.google.common.base.Joiner;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.factoids.FactoidInfoCommand.Input;
import io.shockah.dunlin.factoids.db.Factoid;
import io.shockah.dunlin.util.TimeDuration;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public class FactoidInfoCommand extends NamedCommand<Input, Factoid> {
	private final FactoidsPlugin plugin;
	
	public FactoidInfoCommand(FactoidsPlugin plugin) {
		super("factoid");
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
		
		Factoid.Context context = null;
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
		Factoid factoid = plugin.findActiveFactoid(call.event, input.name, input.context);
		
		if (factoid == null) {
			return CommandResult.of(null, "Factoid doesn't exist.");
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			List<String> lines = new ArrayList<>();
			lines.add(String.format(
					"Factoid: %s | Context: %s | Type: %s | Date: %s UTC (%s ago)",
					factoid.name, factoid.context.name(), factoid.type,
					sdf.format(factoid.date), TimeDuration.format(factoid.date)
			));
			lines.add(String.format("Source: %s", factoid.raw));
			lines.add(String.format("User: %s", plugin.manager.app.getJDA().getUserById(factoid.user).getAsMention()));
			return CommandResult.of(factoid, Joiner.on("\n").join(lines));
		}
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