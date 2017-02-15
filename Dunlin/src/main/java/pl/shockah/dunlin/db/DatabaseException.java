package pl.shockah.dunlin.db;

public class DatabaseException extends RuntimeException {
	private static final long serialVersionUID = 655767348068888205L;

	public DatabaseException(String message) {
		super(message);
	}
	
	public DatabaseException(Throwable cause) {
		super(cause);
	}
	
	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}
}