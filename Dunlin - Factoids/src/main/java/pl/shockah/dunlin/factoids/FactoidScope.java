package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.Where;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.factoids.db.FactoidStore;

import java.sql.SQLException;

public abstract class FactoidScope {
    public final Scope scope;

    public FactoidScope(Scope scope) {
        this.scope = scope;
    }

    public FactoidScope downscope() {
        return null;
    }

    public final String getName() {
        return scope.name();
    }

    public final Factoid getFactoid(FactoidsPlugin plugin, String name) {
        return plugin.manager.app.getDatabaseManager().selectFirst(Factoid.class, qb -> {
            Where<Factoid, Integer> where = qb.where()
                    .eq(Factoid.FORGOTTEN, false).and()
                    .eq(Factoid.NAME, name.toLowerCase());
            fillWhereClause(where);
            qb.orderBy(Factoid.DATE, false);
        });
    }

    public final Factoid rememberFactoid(FactoidsPlugin plugin, FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory, String name, String content, Message message) {
        return plugin.manager.app.getDatabaseManager().create(Factoid.class, obj -> {
            obj.setType(factory.type);
            obj.setName(name.toLowerCase());
            obj.setContent(content);
            obj.setAuthor(message.getAuthor());
            setupFactoidRemember(obj, message);
        });
    }

    protected abstract void fillWhereClause(Where<Factoid, Integer> where) throws SQLException;

    protected abstract void setupFactoidRemember(Factoid factoid, Message message);

    public abstract void setInFactoid(Factoid factoid);

    public abstract void setInFactoidStore(FactoidStore store);
}