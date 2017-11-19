package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.Where;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.db.DatabaseManager;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.factoids.db.FactoidStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;

public abstract class FactoidScope {
    @Nonnull public final Scope scope;

    public FactoidScope(@Nonnull Scope scope) {
        this.scope = scope;
    }

    @Nullable public abstract FactoidScope downscope();

    @Nonnull public final String getName() {
        return scope.name();
    }

    @Nullable public final Factoid getFactoid(@Nonnull FactoidsPlugin plugin, @Nonnull String name) {
        return getFactoid(plugin.manager.app.getDatabaseManager(), name);
    }

    public final Factoid getFactoid(@Nonnull DatabaseManager manager, @Nonnull String name) {
        return manager.selectFirst(Factoid.class, qb -> {
            Where<Factoid, Integer> where = qb.where()
                    .eq(Factoid.FORGOTTEN, false).and()
                    .eq(Factoid.NAME, name.toLowerCase());
            fillWhereClauseForFactoid(where);
            qb.orderBy(Factoid.DATE, false);
        });
    }

    public final FactoidStore getFactoidStore(@Nonnull FactoidsPlugin plugin, @Nonnull String name) {
        return getFactoidStore(plugin.manager.app.getDatabaseManager(), name);
    }

    public final FactoidStore getFactoidStore(@Nonnull DatabaseManager manager, @Nonnull String name) {
        return manager.selectFirst(FactoidStore.class, qb -> {
            Where<FactoidStore, Integer> where = qb.where()
                    .eq(FactoidStore.NAME, name.toLowerCase());
            fillWhereClauseForFactoidStore(where);
            qb.orderBy(Factoid.DATE, false);
        });
    }

    @Nonnull public final Factoid rememberFactoid(@Nonnull FactoidsPlugin plugin, @Nonnull FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory, @Nonnull String name, @Nonnull String content, @Nonnull Message message) {
        return plugin.manager.app.getDatabaseManager().create(Factoid.class, obj -> {
            obj.setType(factory.type);
            obj.setName(name.toLowerCase());
            obj.setContent(content);
            obj.setAuthor(message.getAuthor());
            setupFactoidRemember(obj, message);
        });
    }

    protected abstract void fillWhereClauseForFactoid(@Nonnull Where<Factoid, Integer> where) throws SQLException;

    protected abstract void fillWhereClauseForFactoidStore(@Nonnull Where<FactoidStore, Integer> where) throws SQLException;

    protected abstract void setupFactoidRemember(@Nonnull Factoid factoid, @Nonnull Message message);

    public abstract void setInFactoid(@Nonnull Factoid factoid);

    public abstract void setInFactoidStore(@Nonnull FactoidStore store);
}