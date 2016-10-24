package io.shockah.dunlin.factoids;

import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.factoids.db.Factoid;

public class SimpleFactoidType extends FactoidType {
	public static final String TYPE = "simple";
	
	public SimpleFactoidType() {
		super(TYPE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, R> NamedCommand<T, R> createCommand(Factoid factoid) {
		return (NamedCommand<T, R>)new SimpleFactoidCommand(factoid);
	}
}