package pl.shockah.dunlin.factoids;

import pl.shockah.dunlin.factoids.db.Factoid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class FactoidCommandFactory<T extends AbstractFactoidCommand> {
	@Nonnull public final String type;
	@Nullable public final String codeHighlighting;

	public FactoidCommandFactory(@Nonnull String type, @Nullable String codeHighlighting) {
		this.type = type;
		this.codeHighlighting = codeHighlighting;
	}

	public final T create(@Nonnull Factoid factoid) {
		if (!type.equals(factoid.getType()))
			throw new IllegalArgumentException(String.format("Mismatched factoid type: tried to create `%s`, got `%s`.", type, factoid.getType()));
		return createInternal(factoid);
	}

	@Nonnull protected abstract T createInternal(@Nonnull Factoid factoid);
}