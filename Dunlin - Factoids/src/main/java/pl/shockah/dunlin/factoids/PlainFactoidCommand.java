package pl.shockah.dunlin.factoids;

import net.dv8tion.jda.core.entities.*;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;
import pl.shockah.dunlin.commands.result.ValueCommandResult;
import pl.shockah.dunlin.commands.result.ValueParseResult;
import pl.shockah.dunlin.factoids.db.Factoid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlainFactoidCommand extends AbstractFactoidCommand<String, String> {
    public PlainFactoidCommand(@Nonnull Factoid factoid, @Nonnull String name, @Nonnull String... altNames) {
        super(factoid, name, altNames);
    }

    @Override
    @Nonnull public ParseResult<String> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
        return new ValueParseResult<>(this, textInput);
    }

    @Override
    @Nonnull public CommandResult<String, String> execute(@Nonnull CommandContext context, @Nullable String input) {
        String output = factoid.getContent();
        input = input == null ? "" : input;
        User user = context.message.getAuthor();
        SelfUser bot = context.message.getJDA().getSelfUser();

        Guild guild = null;
        TextChannel channel = null;
        Member member = null;
        if (context.message.getGuild() != null) {
            guild = context.message.getGuild();
            channel = context.message.getTextChannel();
            member = guild.getMember(user);
        }

        output = output.replaceAll("(?iu)%(input|inp)%", input);
        output = output.replaceAll("(?iu)%(usernick|sendernick)%", member == null ? user.getName() : member.getEffectiveName());
        output = output.replaceAll("(?iu)%(userid|senderid)%", user.getId());
        output = output.replaceAll("(?iu)%(username|sendername)%", user.getName());
        output = output.replaceAll("(?iu)%(usermention|sendermention)%", user.getAsMention());
        output = output.replaceAll("(?iu)%(userdiscriminator|senderdiscriminator|usertag|sendertag)%", user.getDiscriminator());
        output = output.replaceAll("(?iu)%botid%", bot.getId());
        output = output.replaceAll("(?iu)%botname%", bot.getName());
        output = output.replaceAll("(?iu)%botmention%", bot.getAsMention());
        output = output.replaceAll("(?iu)%(inputoruserid|ioruid)%", input.isEmpty() ? user.getId() : input);
        output = output.replaceAll("(?iu)%(inputorusername|ioruname)%", input.isEmpty() ? user.getName() : input);
        output = output.replaceAll("(?iu)%(inputorusermention|iorumention)%", input.isEmpty() ? user.getAsMention() : input);
        output = output.replaceAll("(?iu)%(inputoruserdiscriminator|iorudiscriminator|inputorusertag|iorutag)%", input.isEmpty() ? user.getDiscriminator() : input);
        output = output.replaceAll("(?iu)%(inputorusernick|iorunick)%", input.isEmpty() ? (member == null ? user.getName() : member.getEffectiveName()) : input);

        if (guild != null) {
            output = output.replaceAll("(?iu)%guildid%", guild.getId());
            output = output.replaceAll("(?iu)%guildname%", guild.getName());
        }

        if (channel != null) {
            output = output.replaceAll("(?iu)%(channelid|chanid)%", channel.getId());
            output = output.replaceAll("(?iu)%(channelname|channame)%", channel.getName());
            output = output.replaceAll("(?iu)%(channelmention|chanmention)%", channel.getAsMention());
        }

        return new ValueCommandResult<>(this, output);
    }
}