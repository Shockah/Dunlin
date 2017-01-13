package io.shockah.dunlin.commands;

import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public abstract class Command<T, R> {
	public Integer getLineLimit(CommandCall call, T input) {
		/*if (call.inputMedium == CommandCall.Medium.Channel) {
			int maxLines = 5;
			GuildMessageReceivedEvent guildEvent = (GuildMessageReceivedEvent)call.event;
			for (Role role : guildEvent.getGuild().getRolesForUser(guildEvent.getAuthor())) {
				if (role.getPermissions().contains(Permission.MANAGE_CHANNEL)) {
					maxLines = 25;
					break;
				}
			}
			return maxLines;
		}*/
		
		return null;
	}
	
	public T prepareChainedCallInput(GenericMessageEvent e, ValueCommandResult<T> previousResult) {
		return previousResult.value;
	}
	
	public T convertToInput(GenericMessageEvent e, Object input) throws CommandParseException {
		if (input == null)
			return parseInput(e, null);
		return parseInput(e, input.toString());
	}
	
	public abstract T parseInput(GenericMessageEvent e, String input) throws CommandParseException;
	
	public abstract CommandResult<R> call(CommandCall call, T input);
}