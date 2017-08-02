package pl.shockah.dunlin.ircbridge;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.pircbotx.Channel;
import org.pircbotx.User;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;
import pl.shockah.dunlin.commands.result.ValueCommandResult;
import pl.shockah.dunlin.commands.result.ValueParseResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OnlineCommand extends NamedCommand<Channel, List<User>> {
	protected final IRCBridgePlugin ircBridgePlugin;

	public OnlineCommand(IRCBridgePlugin ircBridgePlugin) {
		super("online");
		this.ircBridgePlugin = ircBridgePlugin;
	}

	@Override
	public ParseResult<Channel> parseInput(CommandContext context, String textInput) {
		return new ValueParseResult<>(this, ircBridgePlugin.getIrcChannel(context.message.getTextChannel()));
	}

	@Override
	public CommandResult<Channel, List<User>> execute(CommandContext context, Channel channel) {
		List<User> users = new ArrayList<>(channel.getUsers());
		sortList(channel, users);
		return new ValueCommandResult<>(this, users);
	}

	@Override
	public Message formatOutput(CommandContext context, Channel channel, List<User> users) {
		List<User> ops = users.stream()
				.filter(channel::isOp)
				.collect(Collectors.toList());
		List<User> voiced = users.stream()
				.filter(user -> !ops.contains(user))
				.filter(channel::hasVoice)
				.collect(Collectors.toList());
		List<User> other = users.stream()
				.filter(user -> !ops.contains(user))
				.filter(user -> !voiced.contains(user))
				.collect(Collectors.toList());
		return new MessageBuilder().setEmbed(new EmbedBuilder()
				.addField("Ops", ops.stream()
						.map(User::getNick)
						.collect(Collectors.joining(", ")), true)
				.addField("Voiced", voiced.stream()
						.map(User::getNick)
						.collect(Collectors.joining(", ")), true)
				.addField("Other", other.stream()
						.map(User::getNick)
						.collect(Collectors.joining(", ")), true)
				.build()).build();
	}

	private void sortList(Channel channel, List<User> users) {
		users.sort((u1, u2) -> {
			boolean op1 = channel.isOp(u1);
			boolean op2 = channel.isOp(u2);
			if (op1 != op2)
				return op1 ? -1 : 1;

			boolean voice1 = channel.hasVoice(u1);
			boolean voice2 = channel.hasVoice(u2);
			if (voice1 != voice2)
				return voice1 ? -1 : 1;

			return u1.getNick().compareTo(u2.getNick());
		});
	}
}