package pl.shockah.dunlin.owner;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResultImpl;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.util.TimeDuration;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserCommand extends NamedCommand<User, User> {
	public static final Pattern USER_MENTION = Pattern.compile("\\<\\@\\!?(\\d+)\\>");
	public static final Pattern USER_DISCRIMINATOR = Pattern.compile("(.*?)\\#(\\d{4})");

	protected final OwnerPlugin ownerPlugin;

	public UserCommand(OwnerPlugin ownerPlugin) {
		super("user");
		this.ownerPlugin = ownerPlugin;
	}

	@Override
	public CommandResult<User> parseInput(Message message, String textInput) {
		User user = null;

		if (StringUtils.isBlank(textInput)) {
			user = message.getAuthor();
		} else {
			Matcher matcher;

			if (user == null) {
				matcher = USER_MENTION.matcher(textInput);
				if (matcher.find()) {
					String userId = matcher.group(1);
					user = ownerPlugin.manager.app.getShardManager().getUserById(userId);
				}
			}

			if (user == null) {
				matcher = USER_DISCRIMINATOR.matcher(textInput);
				if (matcher.find()) {
					String name = matcher.group(1);
					String discriminator = matcher.group(2);
					user = Arrays.stream(ownerPlugin.manager.app.getShardManager().shards)
							.map(JDA::getUsers)
							.flatMap(List::stream)
							.filter(u -> u.getName().equalsIgnoreCase(name) && u.getDiscriminator().equals(discriminator))
							.findAny().orElse(null);
				}
			}

			if (user == null) {
				user = message.getGuild().getMembers().stream()
						.filter(m -> m.getEffectiveName().equalsIgnoreCase(textInput))
						.map(Member::getUser)
						.findAny().orElse(null);
			}

			if (user == null) {
				try {
					user = ownerPlugin.manager.app.getShardManager().getUserById((String.valueOf(Long.parseLong(textInput))));
				} catch (Exception e) {
				}
			}
		}

		if (user == null)
			return new ErrorCommandResultImpl<>(this, new MessageBuilder().setEmbed(new EmbedBuilder()
					.setColor(ErrorCommandResultImpl.EMBED_COLOR)
					.setDescription(String.format("Cannot find user `%s`.", textInput))
					.build()).build());

		return new ParseCommandResultImpl<>(this, user);
	}

	@Override
	public CommandResult<User> execute(Message message, User input) {
		return new ValueCommandResultImpl<>(this, input);
	}

	@Override
	public Message formatOutput(Message message, User input, User output) {
		Member member = message.getGuild().getMember(output);

		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setThumbnail(output.getEffectiveAvatarUrl());

		embedBuilder.addField("User", String.format("%s#%s", output.getName(), output.getDiscriminator()), true);
		if (member != null && member.getNickname() != null)
			embedBuilder.addField("Nickname", member.getNickname(), true);
		embedBuilder.addField("ID", output.getId(), true);
		if (member != null)
			embedBuilder.addField("Joined", String.format("%s ago", TimeDuration.format(member.getJoinDate().toLocalDateTime())), true);

		embedBuilder.addField("Registered", String.format("%s ago", TimeDuration.format(output.getCreationTime().toLocalDateTime())), true);

		if (member != null && !member.getRoles().isEmpty())
			embedBuilder.addField("Roles", member.getRoles().stream().map(Role::getAsMention).collect(Collectors.joining(", ")), false);

		return new MessageBuilder().setEmbed(embedBuilder.build()).build();
	}
}