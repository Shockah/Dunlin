package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.Where;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.TextChannelScope;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.factoids.db.FactoidStore;

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
    protected void fillWhereClauseForFactoid(Where<Factoid, Integer> where) throws SQLException {
        where.and()
                .eq(Factoid.SCOPE_TYPE, SCOPE_TYPE).and()
                .eq(Factoid.GUILD_ID, textChannelScope.textChannel.getGuild().getIdLong()).and()
                .eq(Factoid.CHANNEL_ID, textChannelScope.textChannel.getIdLong());
    }

    @Override
    protected void fillWhereClauseForFactoidStore(Where<FactoidStore, Integer> where) throws SQLException {
        where.and()
                .eq(FactoidStore.SCOPE_TYPE, SCOPE_TYPE).and()
                .eq(FactoidStore.GUILD_ID, textChannelScope.textChannel.getGuild().getIdLong()).and()
                .eq(FactoidStore.CHANNEL_ID, textChannelScope.textChannel.getIdLong());
    }

    @Override
    protected void setupFactoidRemember(Factoid factoid, Message message) {
        factoid.setScopeType(SCOPE_TYPE);
        factoid.setChannel(message.getTextChannel());
    }

    @Override
    public void setInFactoid(Factoid factoid) {
        factoid.setScopeType("TextChannel");
        factoid.setChannel(textChannelScope.textChannel);
    }

    @Override
    public void setInFactoidStore(FactoidStore store) {
        store.setScopeType("TextChannel");
        store.setChannel(textChannelScope.textChannel);
    }
}