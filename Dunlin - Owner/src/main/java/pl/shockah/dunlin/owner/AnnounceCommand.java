package pl.shockah.dunlin.owner;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResultImpl;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.settings.GuildSettingScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnounceCommand extends NamedCommand<String, List<TextChannel>> {
    protected final OwnerPlugin ownerPlugin;

    public AnnounceCommand(OwnerPlugin ownerPlugin) {
        super("announce");
        this.ownerPlugin = ownerPlugin;
    }

    @Override
    public CommandResult<String> parseInput(Message message, String textInput) {
        return new ParseCommandResultImpl<>(this, textInput);
    }

    @Override
    public CommandResult<List<TextChannel>> execute(Message message, String input) {
        if (!ownerPlugin.permissionsPlugin.hasPermission(message, ownerPlugin, names[0]))
            return new ErrorCommandResultImpl<>(this, ownerPlugin.permissionsPlugin.buildMissingPermissionMessage(ownerPlugin, names[0]));

        if (StringUtils.isEmpty(input))
            return new ErrorCommandResultImpl<>(this, new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(ErrorCommandResultImpl.EMBED_COLOR)
                    .setDescription("You need to provide a message.")
            .build()).build());

        Message announceMessage = new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle("Announcement", null)
                        .setAuthor(message.getAuthor().getName(), null, message.getAuthor().getEffectiveAvatarUrl())
                        .setDescription(input)
                .build())
        .build();

        List<TextChannel> results = new ArrayList<>();
        Arrays.stream(ownerPlugin.manager.app.getShardManager().shards)
                .map(JDA::getGuilds)
                .flatMap(List::stream)
                .forEach(guild -> {
                    TextChannel channel = null;
                    String channelName = ownerPlugin.announceChannelSetting.get(new GuildSettingScope(message.getGuild()));

                    if (channelName != null)
                        channel = guild.getTextChannelsByName(channelName, true).stream().findFirst().orElse(null);

                    if (channel == null)
                        channel = guild.getTextChannels().stream().filter(guildChannel -> guildChannel.canTalk()).findFirst().orElse(null);

                    if (channel != null) {
                        results.add(channel);
                        channel.sendMessage(announceMessage).queue();
                    }
                });

        ownerPlugin.manager.reload();
        return new ValueCommandResultImpl<>(this, results);
    }

    @Override
    public Message formatOutput(List<TextChannel> channels) {
        return new MessageBuilder().setEmbed(new EmbedBuilder()
                .setColor(ValueCommandResultImpl.EMBED_COLOR)
                .setDescription(String.format("Announced in %d channel%s.", channels.size(), channels.size() == 1 ? "" : "s"))
        .build()).build();
    }
}