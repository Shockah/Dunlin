package pl.shockah.dunlin.groovyfactoids;

import pl.shockah.dunlin.factoids.FactoidCommandFactory;
import pl.shockah.dunlin.factoids.db.Factoid;
import pl.shockah.dunlin.groovyscripting.GroovyScriptingPlugin;

import javax.annotation.Nonnull;

public class GroovyFactoidCommandFactory extends FactoidCommandFactory<GroovyFactoidCommand> {
	@Nonnull public static final String TYPE = "groovy";
	@Nonnull public static final String CODE_HIGHLIGHTING = "groovy";

	@Nonnull public final GroovyScriptingPlugin scriptingPlugin;

	public GroovyFactoidCommandFactory(@Nonnull GroovyScriptingPlugin scriptingPlugin) {
		super(TYPE, CODE_HIGHLIGHTING);
		this.scriptingPlugin = scriptingPlugin;
	}

	@Override
	@Nonnull protected GroovyFactoidCommand createInternal(@Nonnull Factoid factoid) {
		return new GroovyFactoidCommand(scriptingPlugin, factoid, factoid.getName());
	}
}