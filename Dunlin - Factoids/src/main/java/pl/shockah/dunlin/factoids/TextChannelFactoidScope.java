package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.Where;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.TextChannelScope;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.factoids.db.FactoidStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;

public class TextChannelFactoidScope extends FactoidScope {
    @Nonnull public static final String SCOPE_TYPE = "TextChannel";

    @Nonnull public final TextChannelScope textChannelScope;

    public TextChannelFactoidScope(@Nonnull TextChannel textChannel) {
        super(new TextChannelScope(textChannel));
        textChannelScope = (TextChannelScope)scope;
    }

    @Override
    @Nullable public FactoidScope downscope() {
        return new GuildFactoidScope(textChannelScope.textChannel.getGuild());
    }

    @Override
    protected void fillWhereClauseForFactoid(@Nonnull Where<Factoid, Integer> where) throws SQLException {
        where.and()
                .eq(Factoid.SCOPE_TYPE, SCOPE_TYPE).and()
                .eq(Factoid.GUILD_ID, textChannelScope.textChannel.getGuild().getIdLong()).and()
                .eq(Factoid.CHANNEL_ID, textChannelScope.textChannel.getIdLong());
    }

    @Override
    protected void fillWhereClauseForFactoidStore(@Nonnull Where<FactoidStore, Integer> where) throws SQLException {
        where.and()
                .eq(FactoidStore.SCOPE_TYPE, SCOPE_TYPE).and()
                .eq(FactoidStore.GUILD_ID, textChannelScope.textChannel.getGuild().getIdLong()).and()
                .eq(FactoidStore.CHANNEL_ID, textChannelScope.textChannel.getIdLong());
    }

    @Override
    protected void setupFactoidRemember(@Nonnull Factoid factoid, @Nonnull Message message) {
        factoid.setScopeType(SCOPE_TYPE);
        factoid.setChannel(message.getTextChannel());
    }

    @Override
    public void setInFactoid(@Nonnull Factoid factoid) {
        factoid.setScopeType("TextChannel");
        factoid.setChannel(textChannelScope.textChannel);
    }

    @Override
    public void setInFactoidStore(@Nonnull FactoidStore store) {
        store.setScopeType("TextChannel");
        store.setChannel(textChannelScope.textChannel);
    }
}