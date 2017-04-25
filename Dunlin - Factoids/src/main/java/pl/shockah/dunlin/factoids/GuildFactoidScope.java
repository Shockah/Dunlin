package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.QueryBuilder;
import net.dv8tion.jda.core.entities.Guild;
import pl.shockah.dunlin.GuildScope;
import pl.shockah.dunlin.factoids.db.Factoid;

import java.sql.SQLException;

public class GuildFactoidScope extends FactoidScope {
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
    protected void fillWhereClause(QueryBuilder<Factoid, Integer> qb) throws SQLException {
        super.fillWhereClause(qb);
        qb.where().and()
                .eq(Factoid.SCOPE_TYPE, "Guild").and()
                .eq(Factoid.GUILD_ID, guildScope.guild.getId());
    }
}