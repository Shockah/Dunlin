package pl.shockah.dunlin.owner;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import pl.shockah.dunlin.ShardManager;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseResult;
import pl.shockah.dunlin.commands.result.ValueCommandResult;
import pl.shockah.dunlin.commands.result.ValueParseResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StatusCommand extends NamedCommand<Void, StatusCommand.Output> {
    @Nonnull protected final OwnerPlugin ownerPlugin;

    public StatusCommand(@Nonnull OwnerPlugin ownerPlugin) {
        super("status");
        this.ownerPlugin = ownerPlugin;
    }

    @Override
    @Nonnull public ParseResult<Void> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
        return new ValueParseResult<>(this, null);
    }

    @Override
    @Nonnull public CommandResult<Void, Output> execute(@Nonnull CommandContext context, @Nullable Void aVoid) {
        Output output = new Output();
        ShardManager shardManager = ownerPlugin.manager.app.getShardManager();

        output.totalShards = shardManager.shards.length;
        output.connectedShards = (int)Arrays.stream(shardManager.shards).filter(jda -> jda.getStatus() == JDA.Status.CONNECTED).count();

        List<Guild> allGuilds = Arrays.stream(shardManager.shards)
                .map(JDA::getGuilds)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        output.guilds = allGuilds.size();

        output.textChannels = (int)Arrays.stream(shardManager.shards)
                .map(JDA::getTextChannels)
                .mapToLong(List::size)
                .sum();

        output.voiceChannels = (int)Arrays.stream(shardManager.shards)
                .map(JDA::getVoiceChannels)
                .mapToLong(List::size)
                .sum();

        List<User> allUsersAndBots = allGuilds.stream()
                .map(Guild::getMembers)
                .flatMap(List::stream)
                .map(Member::getUser)
                .collect(Collectors.toList());
        List<User> allUsers = allUsersAndBots.stream()
                .filter(user -> !user.isBot())
                .collect(Collectors.toList());
        List<User> allBots = allUsersAndBots.stream()
                .filter(User::isBot)
                .collect(Collectors.toList());

        output.users = allUsers.size();
        output.bots = allBots.size();
        output.uniqueUsers = (int)allUsers.stream().distinct().count();
        output.uniqueBots = (int)allBots.stream().distinct().count();

        List<Member> allOnlineMembers = allGuilds.stream()
                .map(Guild::getMembers)
                .flatMap(List::stream)
                .filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE)
                .collect(Collectors.toList());
        List<Member> allOnlineUserMembers = allOnlineMembers.stream()
                .filter(member -> !member.getUser().isBot())
                .collect(Collectors.toList());
        List<Member> allOnlineBotMembers = allOnlineMembers.stream()
                .filter(member -> member.getUser().isBot())
                .collect(Collectors.toList());

        output.onlineUsers = allOnlineUserMembers.size();
        output.onlineBots = allOnlineBotMembers.size();
        output.uniqueOnlineUsers = (int)allOnlineUserMembers.stream().map(Member::getUser).distinct().count();
        output.uniqueOnlineBots = (int)allOnlineBotMembers.stream().map(Member::getUser).distinct().count();

        output.totalMemory = Runtime.getRuntime().totalMemory();
        output.usedMemory = output.totalMemory - Runtime.getRuntime().freeMemory();

        output.cpuLoad = ((com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad();

        return new ValueCommandResult<>(this, output);
    }

    @Override
    @Nullable public Message formatOutput(@Nonnull CommandContext context, @Nullable Void input, @Nullable Output output) {
        if (output == null)
            throw new IllegalArgumentException();
        SelfUser selfUser = context.message.getJDA().getSelfUser();
        return new MessageBuilder().setEmbed(new EmbedBuilder()
                .setAuthor(selfUser.getName(), null, selfUser.getEffectiveAvatarUrl())
                .addField("Servers", String.format("**Shards:** %d/%d\n**Guilds:** %d", output.connectedShards, output.totalShards, output.guilds), true)
                .addField("Channels", String.format("**Text:** %d\n**Voice:** %d", output.textChannels, output.voiceChannels), true)
                .addField("Users (+ bots)",
                        String.format("**All:** %d + %d\n**Unique:** %d + %d\n**Online:** %d + %d\n**Unique online:** %d + %d",
                                output.users, output.bots,
                                output.uniqueUsers, output.uniqueBots,
                                output.onlineUsers, output.onlineBots,
                                output.uniqueOnlineUsers, output.uniqueOnlineBots),
                        true)
                .addField("Memory", String.format("%.1f/%.1f MB", output.usedMemory / 1024.0 / 1024.0, output.totalMemory / 1024.0 / 1024.0), true)
                .addField("CPU", String.format("%.1f%%", output.cpuLoad * 100.0), true)
                .build()).build();
    }

    public static final class Output {
    	public int connectedShards;
    	public int totalShards;
        public int guilds;

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

        public long usedMemory;
        public long totalMemory;

        public double cpuLoad;
    }
}