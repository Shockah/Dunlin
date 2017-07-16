package pl.shockah.dunlin;

import net.dv8tion.jda.core.entities.TextChannel;

public class TextChannelScope extends Scope {
    public final TextChannel textChannel;

    public TextChannelScope(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    @Override
    public Scope downscope() {
        return new GuildScope(textChannel.getGuild());
    }

    @Override
    public String name() {
        return String.format("Channel: #%s", textChannel.getName());
    }
}