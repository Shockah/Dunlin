package pl.shockah.dunlin.factoids;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.TextChannelScope;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.NamedCommandProvider;
import pl.shockah.dunlin.factoids.db.Factoid;

public class FactoidCommandProvider extends NamedCommandProvider<Object, Object> {
    protected final FactoidsPlugin plugin;

    public FactoidCommandProvider(FactoidsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public NamedCommand<Object, Object> provide(Message message, String name) {
        Factoid factoid = plugin.getFactoid(new TextChannelScope(message.getTextChannel()));
        return null;
    }
}