package pl.shockah.dunlin.factoids;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.NamedCommandProvider;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.util.ReadWriteMap;

import java.util.HashMap;

public class FactoidCommandProvider extends NamedCommandProvider<Object, Object> {
    protected final FactoidsPlugin plugin;
    protected final ReadWriteMap<String, FactoidCommandFactory<? extends FactoidCommand<?, ?>>> factories = new ReadWriteMap<>(new HashMap<>());

    public FactoidCommandProvider(FactoidsPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerFactory(FactoidCommandFactory<? extends FactoidCommand<?, ?>> factory) {
        factories.put(factory.type, factory);
    }

    public void unregisterFactory(FactoidCommandFactory<? extends FactoidCommand<?, ?>> factory) {
        factories.remove(factory.type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public NamedCommand<Object, Object> provide(Message message, String name) {
        Factoid factoid = plugin.getFactoid(new TextChannelFactoidScope(message.getTextChannel()), name);
        if (factoid == null)
            return null;

        FactoidCommandFactory<? extends FactoidCommand<?, ?>> factory = factories.get(factoid.getType());
        if (factory == null)
            throw new IllegalStateException(String.format("Unknown factoid type `%s`.", factoid.getType()));

        return (NamedCommand<Object, Object>)factory.create(factoid);
    }
}