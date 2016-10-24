package io.shockah.dunlin.factoids;

import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.factoids.db.Factoid;

public abstract class AbstractFactoidCommand<T, R> extends NamedCommand<T, R> {
	public final Factoid factoid;
	
	public AbstractFactoidCommand(Factoid factoid) {
		super(factoid.name);
		this.factoid = factoid;
	}
}