package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.Where;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.GuildScope;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.factoids.db.FactoidStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;

public class GuildFactoidScope extends FactoidScope {
    @Nonnull public static final String SCOPE_TYPE = "Guild";

    @Nonnull public final GuildScope guildScope;

    public GuildFactoidScope(@Nonnull Guild guild) {
        super(new GuildScope(guild));
        guildScope = (GuildScope)scope;
    }

    @Override
    @Nullable public FactoidScope downscope() {
        return new GlobalFactoidScope();
    }

    @Override
    protected void fillWhereClauseForFactoid(@Nonnull Where<Factoid, Integer> where) throws SQLException {
        where.and()
                .eq(Factoid.SCOPE_TYPE, SCOPE_TYPE).and()
                .eq(Factoid.GUILD_ID, guildScope.guild.getIdLong());
    }

    @Override
    protected void fillWhereClauseForFactoidStore(@Nonnull Where<FactoidStore, Integer> where) throws SQLException {
        where.and()
                .eq(FactoidStore.SCOPE_TYPE, SCOPE_TYPE).and()
                .eq(FactoidStore.GUILD_ID, guildScope.guild.getIdLong());
    }

    @Override
    protected void setupFactoidRemember(@Nonnull Factoid factoid, @Nonnull Message message) {
        factoid.setScopeType(SCOPE_TYPE);
        factoid.setGuild(message.getGuild());
    }

    @Override
    public void setInFactoid(@Nonnull Factoid factoid) {
        factoid.setScopeType("Guild");
        factoid.setGuild(guildScope.guild);
        factoid.setChannelId(null);
    }

    @Override
    public void setInFactoidStore(@Nonnull FactoidStore store) {
        store.setScopeType("Guild");
        store.setGuild(guildScope.guild);
        store.setChannelId(null);
    }
}