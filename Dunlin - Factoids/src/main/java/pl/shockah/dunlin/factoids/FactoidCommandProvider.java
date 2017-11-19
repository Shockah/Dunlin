package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.NamedCommandProvider;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.util.ReadWriteMap;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class FactoidCommandProvider extends NamedCommandProvider<Object, Object> {
    @Nonnull protected final FactoidsPlugin plugin;
    @Nonnull protected final ReadWriteMap<String, FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>>> factories = new ReadWriteMap<>(new HashMap<>());

    public FactoidCommandProvider(@Nonnull FactoidsPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerFactory(@Nonnull FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory) {
        factories.put(factory.type.toLowerCase(), factory);
    }

    public void unregisterFactory(@Nonnull FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory) {
        factories.remove(factory.type.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    @Override
    public NamedCommand<Object, Object> provide(@Nonnull CommandContext context, @Nonnull String name) {
        Factoid factoid = plugin.getMatchingFactoid(new TextChannelFactoidScope(context.message.getTextChannel()), name);
        if (factoid == null)
            return null;

        FactoidCommandFactory<? extends AbstractFactoidCommand<?, ?>> factory = factories.get(factoid.getType().toLowerCase());
        if (factory == null)
            throw new IllegalStateException(String.format("Unknown factoid type `%s`.", factoid.getType()));

        return (NamedCommand<Object, Object>)factory.create(factoid);
    }
}