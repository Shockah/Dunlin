package pl.shockah.dunlin.groovyfactoids;

import pl.shockah.dunlin.factoids.FactoidCommandFactory;
import pl.shockah.dunlin.factoids.PlainFactoidCommand;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.groovyscripting.GroovyScriptingPlugin;

public class GroovyFactoidCommandFactory extends FactoidCommandFactory<GroovyFactoidCommand> {
	public static final String TYPE = "groovy";

	public final GroovyScriptingPlugin scriptingPlugin;

	public GroovyFactoidCommandFactory(GroovyScriptingPlugin scriptingPlugin) {
		super(TYPE);
		this.scriptingPlugin = scriptingPlugin;
	}

	@Override
	protected GroovyFactoidCommand createInternal(Factoid factoid) {
		return new GroovyFactoidCommand(scriptingPlugin, factoid, factoid.getName());
	}
}