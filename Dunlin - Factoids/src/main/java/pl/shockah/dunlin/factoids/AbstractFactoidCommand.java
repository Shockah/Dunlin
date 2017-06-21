package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.factoids.db.Factoid;

public abstract class AbstractFactoidCommand<Input, Output> extends NamedCommand<Input, Output> {
    protected final Factoid factoid;

    public AbstractFactoidCommand(Factoid factoid, String name, String... altNames) {
        super(name, altNames);
        this.factoid = factoid;
    }
}