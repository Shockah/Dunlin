package io.shockah.dunlin.factoids;

import java.util.Date;
import java.util.regex.Pattern;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.db.DatabaseManager;
import io.shockah.dunlin.factoids.RememberCommand.Input;
import io.shockah.dunlin.factoids.db.Factoid;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;

public class RememberCommand extends NamedCommand<Input, Factoid> {
	private static final Pattern FACTOID_NAME_PATTERN = Pattern.compile("\\w\\S*");
	
	private final FactoidsPlugin plugin;
	
	public RememberCommand(FactoidsPlugin plugin) {
		super("remember", "r");
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
		String[] split = input.split("\\s");
		if (split.length < 2)
			throw new CommandParseException("Not enough arguments.");
		
		Factoid.Context context = null;
		String typeName = null;
		String name = null;
		String raw = null;
		boolean append = false;
		
		for (int i = 0; i < split.length; i++) {
			String arg = split[i];
			if (name == null) {
				if (context == null && arg.charAt(0) == '@') {
					String contextName = arg.substring(1);
					context = Factoid.Context.valueOf(contextName);
					if (context == null)
						throw new CommandParseException(String.format("Invalid factoid context: %s", contextName));
				} else if (typeName == null && arg.charAt(0) == '#') {
					typeName = arg.substring(1);
					FactoidType type = plugin.getType(typeName);
					if (type == null)
						throw new CommandParseException(String.format("Invalid factoid type: %s", typeName));
				} else {
					name = arg;
				}
			} else {
				int length = 0;
				for (int j = 0; j < i; j++) {
					length += split[j].length() + 1;
				}
				raw = input.substring(length);
				break;
			}
		}
		
		if (name == null || raw == null)
			throw new CommandParseException("Not enough arguments.");
		
		if (context == null)
			context = plugin.getDefaultContext();
		if (typeName == null)
			typeName = SimpleFactoidType.TYPE;
		
		if (name.charAt(0) == '+') {
			name = name.substring(1);
			append = true;
		}
		
		return new Input(context, name, typeName, raw, append);
	}

	@Override
	public CommandResult<Factoid> call(CommandCall call, Input input) {
		if (!FACTOID_NAME_PATTERN.matcher(input.name).find())
			throw new IllegalArgumentException("Illegal name.");
		if (input.context == Factoid.Context.Global && !plugin.hasGlobalFactoidPermission(call.event.getAuthor()))
			return CommandResult.error("Permission required.");
		
		DatabaseManager databaseManager = plugin.manager.app.getDatabaseManager();
		
		Guild guild = null;
		TextChannel channel = null;
		if (call.event instanceof GenericGuildMessageEvent) {
			GenericGuildMessageEvent guildMessageEvent = (GenericGuildMessageEvent)call.event;
			guild = guildMessageEvent.getGuild();
			channel = guildMessageEvent.getChannel();
		}
		
		final Guild fguild = guild;
		final TextChannel fchannel = channel;
		
		databaseManager.delete(Factoid.class, (builder, where) -> {
			where
				.equals(Factoid.NAME_COLUMN, input.name)
				.equals(Factoid.ACTIVE_COLUMN, false)
				.equals(Factoid.CONTEXT_COLUMN, input.context);
			if (input.context == Factoid.Context.Channel)
				where
					.equals(Factoid.SERVER_COLUMN, fguild.getId())
					.equals(Factoid.CHANNEL_COLUMN, fchannel.getId());
			else if (input.context == Factoid.Context.Server)
				where.equals(Factoid.SERVER_COLUMN, fguild.getId());
		});
		
		String raw = input.raw;
		if (input.append) {
			Factoid currentFactoid = plugin.findActiveFactoid(call.event, input.name, input.context);
			if (currentFactoid == null)
				return CommandResult.error("Factoid doesn't exist.");
			
			raw = String.format("%s\n%s", currentFactoid.raw, input.raw);
			currentFactoid.update(obj -> {
				obj.active = false;
			});
		}
		
		final String f_raw = raw;
		Factoid factoid = databaseManager.create(Factoid.class, obj -> {
			obj.server = fguild.getId();
			obj.channel = fchannel.getId();
			obj.context = input.context;
			obj.name = input.name;
			obj.type = input.typeName;
			obj.raw = f_raw;
			obj.date = new Date();
			obj.user = call.event.getAuthor().getId();
		});
		
		//if (call.outputMedium == null)
		//	call.outputMedium = Medium.Notice;
		String response = "Done.";
		call.outputMedium = plugin.getMessageMediumForConfirmationMessage(call.inputMedium, response);
		return CommandResult.of(factoid, response);
	}
	
	public static final class Input {
		public final Factoid.Context context;
		public final String name;
		public final String typeName;
		public final String raw;
		public final boolean append;
		
		public Input(Factoid.Context context, String name, String typeName, String raw) {
			this(context, name, typeName, raw, true);
		}
		
		public Input(Factoid.Context context, String name, String typeName, String raw, boolean append) {
			this.context = context;
			this.name = name;
			this.typeName = typeName;
			this.raw = raw;
			this.append = append;
		}
	}
}