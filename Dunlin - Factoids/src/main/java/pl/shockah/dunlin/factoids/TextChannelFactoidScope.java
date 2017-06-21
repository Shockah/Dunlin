package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.QueryBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.TextChannelScope;
import pl.shockah.dunlin.factoids.db.Factoid;

import java.sql.SQLException;

public class TextChannelFactoidScope extends FactoidScope {
    public static final String SCOPE_TYPE = "TextChannel";

    public final TextChannelScope textChannelScope;

    public TextChannelFactoidScope(TextChannel textChannel) {
        super(new TextChannelScope(textChannel));
        textChannelScope = (TextChannelScope)scope;
    }

    @Override
    public FactoidScope downscope() {
        return new GuildFactoidScope(textChannelScope.textChannel.getGuild());
    }

    @Override
    protected void fillWhereClause(QueryBuilder<Factoid, Integer> qb) throws SQLException {
        qb.where().and()
                .eq(Factoid.SCOPE_TYPE, SCOPE_TYPE).and()
                .eq(Factoid.GUILD_ID, textChannelScope.textChannel.getGuild().getId()).and()
                .eq(Factoid.CHANNEL_ID, textChannelScope.textChannel.getId());
    }

    @Override
    protected void setupFactoidRemember(Factoid factoid, Message message) {
        factoid.setScopeType(SCOPE_TYPE);
        factoid.setChannel(message.getTextChannel());
    }
}