package pl.shockah.dunlin.factoids;

import net.dv8tion.jda.core.entities.*;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.factoids.db.Factoid;

public class PlainFactoidCommand extends FactoidCommand<String, String> {
    public PlainFactoidCommand(Factoid factoid, String name, String... altNames) {
        super(factoid, name, altNames);
    }

    @Override
    public CommandResult<String> parseInput(Message message, String textInput) {
        return new ParseCommandResultImpl<>(this, textInput);
    }

    @Override
    public CommandResult<String> execute(Message message, String input) {
        String output = factoid.getContent();
        input = input == null ? "" : input;
        User user = message.getAuthor();
        SelfUser bot = message.getJDA().getSelfUser();

        Guild guild = null;
        TextChannel channel = null;
        Member member = null;
        if (message.getGuild() != null) {
            guild = message.getGuild();
            channel = message.getTextChannel();
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

        return new ValueCommandResultImpl<>(this, output);
    }
}