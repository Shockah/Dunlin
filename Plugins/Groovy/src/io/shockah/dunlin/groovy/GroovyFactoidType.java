package io.shockah.dunlin.groovy;

import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.factoids.FactoidType;
import io.shockah.dunlin.factoids.db.Factoid;

public class GroovyFactoidType extends FactoidType {
	public static final String TYPE = "groovy";
	
	public final GroovyPlugin plugin;
	
	public GroovyFactoidType(GroovyPlugin plugin) {
		super(TYPE);
		this.plugin = plugin;
	}

	@Override
	public <T, R> NamedCommand<T, R> createCommand(Factoid factoid) {
		return new GroovyFactoidCommand<T, R>(plugin, factoid);
	}
}