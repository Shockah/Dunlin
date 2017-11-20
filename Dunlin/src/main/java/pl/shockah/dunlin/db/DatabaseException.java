package pl.shockah.dunlin.db;

import javax.annotation.Nonnull;

public class DatabaseException extends RuntimeException {
	private static final long serialVersionUID = 655767348068888205L;

	public DatabaseException(@Nonnull String message) {
		super(message);
	}
	
	public DatabaseException(@Nonnull Throwable cause) {
		super(cause);
	}
	
	public DatabaseException(@Nonnull String message, @Nonnull Throwable cause) {
		super(message, cause);
	}
}