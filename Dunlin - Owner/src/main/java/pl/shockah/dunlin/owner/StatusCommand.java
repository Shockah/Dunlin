package pl.shockah.dunlin.owner;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.SelfUser;
import pl.shockah.dunlin.ShardManager;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StatusCommand extends NamedCommand<Void, StatusCommand.Output> {
    protected final OwnerPlugin ownerPlugin;

    public StatusCommand(OwnerPlugin ownerPlugin) {
        super("status");
        this.ownerPlugin = ownerPlugin;
    }

    @Override
    public CommandResult<Void> parseInput(Message message, String textInput) {
        return new ParseCommandResultImpl<>(this, null);
    }

    @Override
    public CommandResult<StatusCommand.Output> execute(Message message, Void aVoid) {
        Output output = new Output();
        ShardManager shardManager = ownerPlugin.manager.app.getShardManager();

        List<Guild> allGuilds = Arrays.stream(shardManager.shards)
                .map(JDA::getGuilds)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        output.servers = allGuilds.size();

        output.textChannels = (int)Arrays.stream(shardManager.shards)
                .map(JDA::getTextChannels)
                .flatMap(List::stream)
                .count();

        output.voiceChannels = (int)Arrays.stream(shardManager.shards)
                .map(JDA::getVoiceChannels)
                .flatMap(List::stream)
                .count();

        output.users = (int)Arrays.stream(shardManager.shards)
                .map(JDA::getUsers)
                .flatMap(List::stream)
                .filter(user -> !user.isBot())
                .count();

        output.uniqueUsers = (int)Arrays.stream(shardManager.shards)
                .map(JDA::getUsers)
                .flatMap(List::stream)
                .filter(user -> !user.isBot())
                .distinct()
                .count();

        output.bots = (int)Arrays.stream(shardManager.shards)
                .map(JDA::getUsers)
                .flatMap(List::stream)
                .filter(user -> user.isBot())
                .count();

        output.uniqueBots = (int)Arrays.stream(shardManager.shards)
                .map(JDA::getUsers)
                .flatMap(List::stream)
                .filter(user -> user.isBot())
                .distinct()
                .count();

        output.onlineUsers = (int)allGuilds.stream()
                .map(Guild::getMembers)
                .flatMap(List::stream)
                .filter(member -> !member.getUser().isBot())
                .filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE)
                .count();

        output.onlineBots = (int)allGuilds.stream()
                .map(Guild::getMembers)
                .flatMap(List::stream)
                .filter(member -> member.getUser().isBot())
                .filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE)
                .count();

        output.uniqueOnlineUsers = (int)allGuilds.stream()
                .map(Guild::getMembers)
                .flatMap(List::stream)
                .filter(member -> !member.getUser().isBot())
                .filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE)
                .map(Member::getUser)
                .distinct()
                .count();

        output.uniqueOnlineBots = (int)allGuilds.stream()
                .map(Guild::getMembers)
                .flatMap(List::stream)
                .filter(member -> member.getUser().isBot())
                .filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE)
                .map(Member::getUser)
                .distinct()
                .count();

        return new ValueCommandResultImpl<>(this, output);
    }

    @Override
    public Message formatOutput(Message message, Void input, Output output) {
        SelfUser selfUser = message.getJDA().getSelfUser();
        return new MessageBuilder().setEmbed(new EmbedBuilder()
                .setAuthor(selfUser.getName(), null, selfUser.getEffectiveAvatarUrl())
                .addField("Servers", String.valueOf(output.servers), true)
                .addField("Channels", String.format("Text: %d\nVoice: %d", output.textChannels, output.voiceChannels), true)
                .addField("Users (+ bots)",
                        String.format("All: %d + %d\nUnique: %d + %d\nOnline: %d + %d\nUnique online: %d + %d",
                                output.users, output.bots,
                                output.uniqueUsers, output.uniqueBots,
                                output.onlineUsers, output.onlineBots,
                                output.uniqueOnlineUsers, output.uniqueOnlineBots),
                        true)
                .build()).build();
    }

    public static final class Output {
        public int servers;

        public int textChannels;
        public int voiceChannels;

        public int users;
        public int uniqueUsers;
        public int onlineUsers;
        public int uniqueOnlineUsers;

        public int bots;
        public int uniqueBots;
        public int onlineBots;
        public int uniqueOnlineBots;
    }
}