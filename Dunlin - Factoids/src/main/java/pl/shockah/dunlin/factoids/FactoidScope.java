package pl.shockah.dunlin.factoids;

import com.j256.ormlite.stmt.Where;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.factoids.db.Factoid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;

public abstract class FactoidScope {
    @Nonnull public final Scope scope;

    public FactoidScope(@Nonnull Scope scope) {
        this.scope = scope;
    }

    @Nullable public FactoidScope downscope() {
        return null;
    }

    @Nonnull public final String getName() {
        return scope.name();
    }

    @Nullable public final Factoid getFactoid(@Nonnull FactoidsPlugin plugin, @Nonnull String name) {
        return plugin.manager.app.getDatabaseManager().selectFirst(Factoid.class, qb -> {
            Where<Factoid, Integer> where = qb.where()
                    .eq(Factoid.FORGOTTEN, false).and()
                    .eq(Factoid.NAME, name.toLowerCase());
            fillWhereClause(where);
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

    protected abstract void fillWhereClause(@Nonnull Where<Factoid, Integer> where) throws SQLException;

    protected abstract void setupFactoidRemember(@Nonnull Factoid factoid, @Nonnull Message message);
}