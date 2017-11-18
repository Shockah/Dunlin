package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.Where;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.GlobalScope;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.factoids.db.FactoidStore;

import java.sql.SQLException;

public class GlobalFactoidScope extends FactoidScope {
    public static final String SCOPE_TYPE = "Global";

    public GlobalFactoidScope() {
        super(new GlobalScope());
    }

    @Override
    public FactoidScope downscope() {
        return null;
    }

    @Override
    protected void fillWhereClauseForFactoid(Where<Factoid, Integer> where) throws SQLException {
        where.and()
                .eq(Factoid.SCOPE_TYPE, SCOPE_TYPE);
    }

    @Override
    protected void fillWhereClauseForFactoidStore(Where<FactoidStore, Integer> where) throws SQLException {
        where.and()
                .eq(FactoidStore.SCOPE_TYPE, SCOPE_TYPE);
    }

    @Override
    protected void setupFactoidRemember(Factoid factoid, Message message) {
        factoid.setScopeType(SCOPE_TYPE);
    }

    @Override
    public void setInFactoid(Factoid factoid) {
        factoid.setScopeType("Global");
        factoid.setGuildId(null);
        factoid.setChannelId(null);
    }

    @Override
    public void setInFactoidStore(FactoidStore store) {
        store.setScopeType("Global");
        store.setGuildId(null);
        store.setChannelId(null);
    }
}