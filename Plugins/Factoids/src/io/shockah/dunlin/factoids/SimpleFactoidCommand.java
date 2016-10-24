package io.shockah.dunlin.factoids;

import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.factoids.db.Factoid;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.SelfInfo;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.GenericMessageEvent;
import net.dv8tion.jda.events.message.guild.GenericGuildMessageEvent;

public class SimpleFactoidCommand extends AbstractFactoidCommand<String, String> {
	public SimpleFactoidCommand(Factoid factoid) {
		super(factoid);
	}
	
	@Override
	public String prepareChainedCallInput(GenericMessageEvent e, CommandResult<String> previousResult) {
		return previousResult.toString();
	}
	
	@Override
	public String parseInput(GenericMessageEvent e, String input) throws CommandParseException {
		return input;
	}

	@Override
	public CommandResult<String> call(CommandCall call, String input) {
		String output = factoid.raw;
		input = input == null ? "" : input;
		User user = call.event.getAuthor();
		SelfInfo bot = call.event.getJDA().getSelfInfo();
		
		Guild guild = null;
		TextChannel channel = null;
		if (call.event instanceof GenericGuildMessageEvent) {
			GenericGuildMessageEvent guildMessageEvent = (GenericGuildMessageEvent)call.event;
			guild = guildMessageEvent.getGuild();
			channel = guildMessageEvent.getChannel();
		}
		
		output = output.replaceAll("(?iu)\\%(input|inp)\\%", input);
		output = output.replaceAll("(?iu)\\%(userid|senderid)\\%", user.getId());
		output = output.replaceAll("(?iu)\\%(username|sendername)\\%", user.getUsername());
		output = output.replaceAll("(?iu)\\%(usermention|sendermention)\\%", user.getAsMention());
		output = output.replaceAll("(?iu)\\%botid\\%", bot.getId());
		output = output.replaceAll("(?iu)\\%botname\\%", bot.getUsername());
		output = output.replaceAll("(?iu)\\%botmention\\%", bot.getAsMention());
		output = output.replaceAll("(?iu)\\%(inputoruserid|ioruid)\\%", input.isEmpty() ? user.getId() : input);
		output = output.replaceAll("(?iu)\\%(inputorusername|ioruname)\\%", input.isEmpty() ? user.getUsername() : input);
		output = output.replaceAll("(?iu)\\%(inputorusermention|iorumention)\\%", input.isEmpty() ? user.getAsMention() : input);
		
		if (guild != null) {
			output = output.replaceAll("(?iu)\\%(serverid|srvid)\\%", guild.getId());
			output = output.replaceAll("(?iu)\\%(servername|srvname)\\%", guild.getName());
		}
		
		if (channel != null) {
			output = output.replaceAll("(?iu)\\%(channelid|chanid)\\%", channel.getId());
			output = output.replaceAll("(?iu)\\%(channelname|channame)\\%", channel.getName());
			output = output.replaceAll("(?iu)\\%(channelmention|chanmention)\\%", channel.getAsMention());
		}
		
		return CommandResult.of(output);
	}
}