package pl.shockah.dunlin.owner;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.lang3.StringUtils;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResultImpl;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.util.TimeDuration;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserCommand extends NamedCommand<Member, Member> {
	public static final Pattern USER_MENTION = Pattern.compile("\\<\\@(\\d+)\\>");
	public static final Pattern USER_DISCRIMINATOR = Pattern.compile("(.*?)\\#(\\d{4})");

	protected final OwnerPlugin ownerPlugin;

	public UserCommand(OwnerPlugin ownerPlugin) {
		super("user");
		this.ownerPlugin = ownerPlugin;
	}

	@Override
	public CommandResult<Member> parseInput(Message message, String textInput) {
		Member member = null;

		if (StringUtils.isBlank(textInput)) {
			member = message.getGuild().getMember(message.getAuthor());
		} else {
			Matcher matcher;

			if (member == null) {
				matcher = USER_MENTION.matcher(textInput);
				if (matcher.find()) {
					String userId = matcher.group(1);
					member = message.getGuild().getMemberById(userId);
				}
			}

			if (member == null) {
				matcher = USER_DISCRIMINATOR.matcher(textInput);
				if (matcher.find()) {
					String name = matcher.group(1);
					String discriminator = matcher.group(2);
					member = message.getGuild().getMembers().stream()
							.filter(m -> m.getUser().getName().equalsIgnoreCase(name) && m.getUser().getDiscriminator().equals(discriminator))
							.findAny().get();
				}
			}

			if (member == null) {
				member = message.getGuild().getMembers().stream()
						.filter(m -> m.getEffectiveName().equalsIgnoreCase(textInput))
						.findAny().get();
			}
		}

		if (member == null)
			return new ErrorCommandResultImpl<>(this, new MessageBuilder().setEmbed(new EmbedBuilder()
					.setColor(ErrorCommandResultImpl.EMBED_COLOR)
					.setDescription(String.format("Cannot find user `%s`.", textInput))
					.build()).build());

		return new ParseCommandResultImpl<>(this, member);
	}

	@Override
	public CommandResult<Member> execute(Message message, Member input) {
		return new ValueCommandResultImpl<>(this, input);
	}

	@Override
	public Message formatOutput(Message message, Member input, Member output) {
		return new MessageBuilder().setEmbed(new EmbedBuilder()
				.setThumbnail(output.getUser().getEffectiveAvatarUrl())
				.addField("Nickname", output.getEffectiveName(), true)
				.addField("User", String.format("%s#%s", output.getUser().getName(), output.getUser().getDiscriminator()), true)
				.addField("ID", output.getUser().getId(), true)
				.addField("Joined", String.format("%s (%s ago)",
						output.getJoinDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
						TimeDuration.format(Date.from(output.getJoinDate().toInstant()))),
						true)
				.addField("Roles", output.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")), false)
				.build()).build();
	}
}