package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.QueryBuilder;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.factoids.db.Factoid;

import java.sql.SQLException;

public abstract class FactoidScope {
    public final Scope scope;

    public FactoidScope(Scope scope) {
        this.scope = scope;
    }

    public FactoidScope downscope() {
        return null;
    }

    public final String name() {
        return scope.name();
    }

    public final Factoid getFactoid(FactoidsPlugin plugin, String name) {
        return plugin.manager.app.getDatabaseManager().selectFirst(Factoid.class, qb -> {
            qb.where().eq(Factoid.FORGOTTEN, false);
            fillWhereClause(qb);
            qb.orderBy(Factoid.DATE, false);
        });
    }

    protected void fillWhereClause(QueryBuilder<Factoid, Integer> qb) throws SQLException {
    }
}