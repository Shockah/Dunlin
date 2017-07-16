package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.Where;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.GuildScope;
import pl.shockah.dunlin.factoids.db.Factoid;

import java.sql.SQLException;

public class GuildFactoidScope extends FactoidScope {
    public static final String SCOPE_TYPE = "Guild";

    public final GuildScope guildScope;

    public GuildFactoidScope(Guild guild) {
        super(new GuildScope(guild));
        guildScope = (GuildScope)scope;
    }

    @Override
    public FactoidScope downscope() {
        return new GlobalFactoidScope();
    }

    @Override
    protected void fillWhereClause(Where<Factoid, Integer> where) throws SQLException {
        where.and()
                .eq(Factoid.SCOPE_TYPE, SCOPE_TYPE).and()
                .eq(Factoid.GUILD_ID, guildScope.guild.getIdLong());
    }

    @Override
    protected void setupFactoidRemember(Factoid factoid, Message message) {
        factoid.setScopeType(SCOPE_TYPE);
        factoid.setGuild(message.getGuild());
    }
}