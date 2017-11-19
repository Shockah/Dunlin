package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.factoids.db.Factoid;

import javax.annotation.Nonnull;

public abstract class AbstractFactoidCommand<Input, Output> extends NamedCommand<Input, Output> {
    @Nonnull protected final Factoid factoid;

    public AbstractFactoidCommand(@Nonnull Factoid factoid, @Nonnull String name, @Nonnull String... altNames) {
        super(name, altNames);
        this.factoid = factoid;
    }
}