package io.shockah.dunlin.poll;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.shockah.dunlin.argparser.ArgumentSchema;
import io.shockah.dunlin.argparser.ArgumentType;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.commands.ErrorCommandResult;
import io.shockah.dunlin.commands.ExceptionCommandResult;
import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.commands.ValueCommandResult;
import io.shockah.dunlin.db.DatabaseManager;
import io.shockah.dunlin.poll.db.Poll;
import io.shockah.dunlin.poll.db.PollOption;
import io.shockah.dunlin.util.TimeDuration;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import pl.shockah.util.UnexpectedException;

public class PollCommand extends NamedCommand<PollCommand.Input, Poll> {
	public static final long DEFAULT_DURATION = 1000l * 60l * 5l; //5 minutes
	public static final Pattern CUSTOM_EMOTE_PATTERN = Pattern.compile("<:(.*?):(\\d+?)>");
	
	private final PollPlugin plugin;
	private final ArgumentSchema schema;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public PollCommand(PollPlugin plugin) {
		super("poll");
		this.plugin = plugin;
		
		schema = new ArgumentSchema.Builder()
			.with("duration", ArgumentType.String)
			.with("options", ArgumentType.String)
			.with("option", ArgumentType.StringList)
			.with("multiple", ArgumentType.Bool)
			.withUnnamedValue(true)
			.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Input parseInput(GenericMessageEvent e, String input) throws CommandParseException {
		Map<String, Object> args = schema.parse(input);
		if (args.containsKey("options")) {
			if (args.containsKey("option"))
				throw new CommandParseException("Use either 'options' or multiple 'option' arguments.", true);
			
			args.put("option", Arrays.asList(((String)args.get("options")).split("\\s")));
			args.remove("options");
		}
		
		String name = args.containsKey("") ? (String)args.get("") : String.format("Poll %s", dateFormat.format(new Date()));
		Date date = new Date(new Date().getTime() + (args.containsKey("duration") ? TimeDuration.parseSeconds((String)args.get("duration")) * 1000 : DEFAULT_DURATION));
		boolean multipleChoice = args.containsKey("multiple") ? (boolean)args.get("multiple") : false;
		
		List<Input.Option> options = new ArrayList<>();
		if (args.containsKey("option")) {
			List<String> optionStrings = (List<String>)args.get("option");
			for (String optionString : optionStrings) {
				String[] split = optionString.split(" ");
				String emoteString = split[0];
				String optionName = null;
				if (split.length >= 2)
					optionName = optionString.substring(split[0].length() + 1).trim();
				
				options.add(new Input.Option(emoteString, optionName));
			}
		}
		
		return new Input(name, date, options, multipleChoice);
	}

	@Override
	public CommandResult<Poll> call(CommandCall call, Input input) {
		if (!(call.event instanceof GenericGuildMessageEvent))
			return new ErrorCommandResult<>("This command can only be called in server text channels.");
		
		if (input.date.getTime() - new Date().getTime() < 1000l * 9l)
			return new ErrorCommandResult<>("Can't create a poll shorter than 10 seconds.");
		
		try {
			GenericGuildMessageEvent event = (GenericGuildMessageEvent)call.event;
			Message message = event.getChannel().sendMessage(getMessageForInput(input)).block();
			
			DatabaseManager databaseManager = plugin.manager.app.getDatabaseManager();
			Poll poll = databaseManager.create(Poll.class, obj -> {
				obj.channel = event.getChannel().getId();
				obj.date = input.date;
				obj.name = input.name;
				obj.multipleChoice = input.multipleChoice;
				obj.message = message.getId();
			});
			for (Input.Option option : input.options) {
				databaseManager.create(PollOption.class, obj -> {
					obj.emote = option.emote;
					obj.name = option.name;
					obj.setPoll(poll);
				});
				
				Matcher m = CUSTOM_EMOTE_PATTERN.matcher(option.emote);
				if (m.find())
					message.addReaction(message.getJDA().getEmoteById(m.group(2))).queue();
				else
					message.addReaction(option.emote).queue();
			}
			
			try {
				poll.refresh();
			} catch (SQLException e) {
				throw new UnexpectedException(e);
			}
			poll.finishOrSchedule(plugin);
			return new ValueCommandResult<>(poll, "");
		} catch (RateLimitedException e) {
			return new ExceptionCommandResult<>(e);
		}
	}
	
	private String getMessageForInput(Input input) {
		return String.format("__**Poll:**__\n**%s**\nVoting ends in %s!", input.name, TimeDuration.format(input.date, false));
	}
	
	public static final class Input {
		public final String name;
		public final Date date;
		public final List<Option> options;
		public final boolean multipleChoice;
		
		public Input(String name, Date date, List<Option> options, boolean multipleChoice) {
			this.name = name;
			this.date = date;
			this.options = options;
			this.multipleChoice = multipleChoice;
		}
		
		public static final class Option {
			public final String emote;
			public final String name;
			
			public Option(String emote, String name) {
				this.emote = emote;
				this.name = name;
			}
		}
	}
}