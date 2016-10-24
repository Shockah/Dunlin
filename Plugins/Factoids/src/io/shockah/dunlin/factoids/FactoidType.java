package io.shockah.dunlin.factoids;

import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.factoids.db.Factoid;

public abstract class FactoidType {
	public final String type;
	
	public FactoidType(String type) {
		this.type = type;
	}
	
	public abstract <T, R> NamedCommand<T, R> createCommand(Factoid factoid);
}