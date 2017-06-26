package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.QueryBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.GlobalScope;
import pl.shockah.dunlin.factoids.db.Factoid;

import java.sql.SQLException;

public class GlobalFactoidScope extends FactoidScope {
    public static final String SCOPE_TYPE = "Global";

    public GlobalFactoidScope() {
        super(new GlobalScope());
    }

    @Override
    protected void fillWhereClause(QueryBuilder<Factoid, Integer> qb) throws SQLException {
        qb.where()
                .eq(Factoid.SCOPE_TYPE, SCOPE_TYPE);
    }

    @Override
    protected void setupFactoidRemember(Factoid factoid, Message message) {
        factoid.setScopeType(SCOPE_TYPE);
    }
}