package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.Where;
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
    protected void fillWhereClause(Where<Factoid, Integer> where) throws SQLException {
        where.and()
                .eq(Factoid.SCOPE_TYPE, SCOPE_TYPE);
    }

    @Override
    protected void setupFactoidRemember(Factoid factoid, Message message) {
        factoid.setScopeType(SCOPE_TYPE);
    }
}