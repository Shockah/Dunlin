package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.QueryBuilder;
import pl.shockah.dunlin.GlobalScope;
import pl.shockah.dunlin.factoids.db.Factoid;

import java.sql.SQLException;

public class GlobalFactoidScope extends FactoidScope {
    public GlobalFactoidScope() {
        super(new GlobalScope());
    }

    @Override
    protected void fillWhereClause(QueryBuilder<Factoid, Integer> qb) throws SQLException {
        super.fillWhereClause(qb);
        qb.where().and()
                .eq(Factoid.SCOPE_TYPE, "Global");
    }
}