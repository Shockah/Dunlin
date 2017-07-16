package pl.shockah.dunlin;

import net.dv8tion.jda.core.entities.Guild;

public class GuildScope extends Scope {
    public final Guild guild;

    public GuildScope(Guild guild) {
        this.guild = guild;
    }

    @Override
    public Scope downscope() {
        return new GlobalScope();
    }

    @Override
    public String name() {
        return String.format("Guild: %s", guild.getName());
    }
}