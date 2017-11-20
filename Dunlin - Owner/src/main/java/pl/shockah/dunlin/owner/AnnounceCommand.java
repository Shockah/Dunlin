package pl.shockah.dunlin.owner;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.*;
import pl.shockah.dunlin.settings.GuildSettingScope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnounceCommand extends NamedCommand<String, List<TextChannel>> {
    @Nonnull protected final OwnerPlugin ownerPlugin;

    public AnnounceCommand(@Nonnull OwnerPlugin ownerPlugin) {
        super("announce");
        this.ownerPlugin = ownerPlugin;
    }

    @Override
    @Nonnull public ParseResult<String> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
        return new ValueParseResult<>(this, textInput);
    }

    @Override
    @Nonnull public CommandResult<String, List<TextChannel>> execute(@Nonnull CommandContext context, @Nullable String input) {
        if (!ownerPlugin.permissionsPlugin.hasPermission(context.message, ownerPlugin, names[0]))
            return new ErrorCommandResult<>(this, ownerPlugin.permissionsPlugin.buildMissingPermissionMessage(ownerPlugin, names[0]));

        if (StringUtils.isEmpty(input))
            return new ErrorCommandResult<>(this, new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(ErrorCommandResult.EMBED_COLOR)
                    .setDescription("You need to provide a message.")
            .build()).build());

        Message announceMessage = new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle("Announcement", null)
                        .setAuthor(context.message.getAuthor().getName(), null, context.message.getAuthor().getEffectiveAvatarUrl())
                        .setDescription(input)
                .build())
        .build();

        List<TextChannel> results = new ArrayList<>();
        Arrays.stream(ownerPlugin.manager.app.getShardManager().shards)
                .map(JDA::getGuilds)
                .flatMap(List::stream)
                .forEach(guild -> {
                    TextChannel channel = null;
                    String channelName = ownerPlugin.announceChannelSetting.get(new GuildSettingScope(context.message.getGuild()));

                    if (channelName != null)
                        channel = guild.getTextChannelsByName(channelName, true).stream().findFirst().orElse(null);

                    if (channel == null)
                        channel = guild.getTextChannels().stream().filter(TextChannel::canTalk).findFirst().orElse(null);

                    if (channel != null) {
                        results.add(channel);
                        channel.sendMessage(announceMessage).queue();
                    }
                });

        return new ValueCommandResult<>(this, results);
    }

    @Override
    @Nullable public Message formatOutput(@Nullable List<TextChannel> channels) {
        if (channels == null)
            throw new IllegalArgumentException();
        return new MessageBuilder().setEmbed(new EmbedBuilder()
                .setColor(ValueCommandResult.EMBED_COLOR)
                .setDescription(String.format("Announced in %d channel%s.", channels.size(), channels.size() == 1 ? "" : "s"))
        .build()).build();
    }
}