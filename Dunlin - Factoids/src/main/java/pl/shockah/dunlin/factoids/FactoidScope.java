package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.Where;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.db.DatabaseManager;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.factoids.db.FactoidStore;

import java.sql.SQLException;

public abstract class FactoidScope {
    public final Scope scope;

    public FactoidScope(Scope scope) {
        this.scope = scope;
    }

    public abstract FactoidScope downscope();

    public final String getName() {
        return scope.name();
    }

    public final Factoid getFactoid(FactoidsPlugin plugin, String name) {
        return getFactoid(plugin.manager.app.getDatabaseManager(), name);
    }

    public final Factoid getFactoid(DatabaseManager manager, String name) {
        return manager.selectFirst(Factoid.class, qb -> {
            Where<Factoid, Integer> where = qb.where()
                    .eq(Factoid.FORGOTTEN, false).and()
                    .eq(Factoid.NAME, name.toLowerCase());
            fillWhereClauseForFactoid(where);
            qb.orderBy(Factoid.DATE, false);
        });
    }

    public final FactoidStore getFactoidStore(FactoidsPlugin plugin, String name) {
        return getFactoidStore(plugin.manager.app.getDatabaseManager(), name);
    }

    public final FactoidStore getFactoidStore(DatabaseManager manager, String name) {
        return manager.selectFirst(FactoidStore.class, qb -> {
            Where<FactoidStore, Integer> where = qb.where()
                    .eq(FactoidStore.NAME, name.toLowerCase());
            fillWhereClauseForFactoidStore(where);
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

    protected abstract void fillWhereClauseForFactoid(Where<Factoid, Integer> where) throws SQLException;

    protected abstract void fillWhereClauseForFactoidStore(Where<FactoidStore, Integer> where) throws SQLException;

    protected abstract void setupFactoidRemember(Factoid factoid, Message message);

    public abstract void setInFactoid(Factoid factoid);

    public abstract void setInFactoidStore(FactoidStore store);
}