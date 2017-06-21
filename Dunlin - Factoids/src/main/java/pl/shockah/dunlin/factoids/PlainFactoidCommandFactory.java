package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.factoids.db.Factoid;

public class PlainFactoidCommandFactory extends FactoidCommandFactory<PlainFactoidCommand> {
	public static final String TYPE = "plain";

	public PlainFactoidCommandFactory() {
		super(TYPE);
	}

	@Override
	protected PlainFactoidCommand createInternal(Factoid factoid) {
		return new PlainFactoidCommand(factoid, factoid.getName());
	}
}