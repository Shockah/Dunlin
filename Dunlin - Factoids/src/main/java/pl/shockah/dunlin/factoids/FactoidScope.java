package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.QueryBuilder;
import net.dv8tion.jda.core.entities.Message;
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
            qb.where()
                    .eq(Factoid.FORGOTTEN, false).and()
                    .eq(Factoid.NAME, name.toLowerCase());
            fillWhereClause(qb);
            qb.orderBy(Factoid.DATE, false);
        });
    }

    public final Factoid rememberFactoid(FactoidsPlugin plugin, FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory, String name, String content, Message message) {
        return plugin.manager.app.getDatabaseManager().create(Factoid.class, obj -> {
            obj.setType(factory.type);
            obj.setName(name.toLowerCase());
            obj.setContent(content);
            setupFactoidRemember(obj, message);
        });
    }

    protected abstract void fillWhereClause(QueryBuilder<Factoid, Integer> qb) throws SQLException;

    protected abstract void setupFactoidRemember(Factoid factoid, Message message);
}