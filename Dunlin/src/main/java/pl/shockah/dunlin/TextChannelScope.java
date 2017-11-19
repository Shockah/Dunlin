package pl.shockah.dunlin;

import net.dv8tion.jda.core.entities.TextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TextChannelScope extends Scope {
    @Nonnull public final TextChannel textChannel;

    public TextChannelScope(@Nonnull TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    @Override
    @Nullable public Scope downscope() {
        return new GuildScope(textChannel.getGuild());
    }

    @Override
    @Nonnull public String name() {
        return String.format("Channel: #%s", textChannel.getName());
    }
}