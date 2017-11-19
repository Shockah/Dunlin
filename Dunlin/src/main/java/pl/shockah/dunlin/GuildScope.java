package pl.shockah.dunlin;

import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuildScope extends Scope {
    @Nonnull public final Guild guild;

    public GuildScope(@Nonnull Guild guild) {
        this.guild = guild;
    }

    @Override
    @Nullable public Scope downscope() {
        return new GlobalScope();
    }

    @Override
    @Nonnull public String name() {
        return String.format("Guild: %s", guild.getName());
    }
}