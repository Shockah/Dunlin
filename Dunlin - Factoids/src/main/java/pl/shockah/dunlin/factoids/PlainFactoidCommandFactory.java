package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.factoids.db.Factoid;

import javax.annotation.Nonnull;

public class PlainFactoidCommandFactory extends FactoidCommandFactory<PlainFactoidCommand> {
	@Nonnull public static final String TYPE = "plain";

	public PlainFactoidCommandFactory() {
		super(TYPE, null);
	}

	@Override
	@Nonnull protected PlainFactoidCommand createInternal(@Nonnull Factoid factoid) {
		return new PlainFactoidCommand(factoid, factoid.getName());
	}
}