package pl.shockah.dunlin.util;

import javax.annotation.Nonnull;
import java.util.Date;

public final class Dates {
	@Nonnull public static Date inFuture(long time) {
		return new Date(new Date().getTime() + time);
	}
	
	@Nonnull public static Date inPast(long time) {
		return new Date(new Date().getTime() - time);
	}
	
	public static boolean isInFuture(@Nonnull Date date) {
		return new Date().before(date);
	}
	
	public static boolean isInPast(@Nonnull Date date) {
		return new Date().after(date);
	}
	
	private Dates() {
	}
}