package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.factoids.db.Factoid;

public abstract class FactoidCommandFactory<T extends AbstractFactoidCommand> {
	public final String type;
	public final String codeHighlighting;

	public FactoidCommandFactory(String type, String codeHighlighting) {
		this.type = type;
		this.codeHighlighting = codeHighlighting;
	}

	public final T create(Factoid factoid) {
		if (!type.equals(factoid.getType()))
			throw new IllegalArgumentException(String.format("Mismatched factoid type: tried to create `%s`, got `%s`.", type, factoid.getType()));
		return createInternal(factoid);
	}

	protected abstract T createInternal(Factoid factoid);
}