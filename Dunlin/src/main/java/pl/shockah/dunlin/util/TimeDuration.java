package pl.shockah.dunlin.util;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeDuration {
	public static final Pattern TIME_DURATION_PATTERN = Pattern.compile("(\\d+)([smhdy]|mo)(\\s?\\d+[smhdy]|mo)*", Pattern.CASE_INSENSITIVE);
	public static final Pattern TIME_DURATION_TOKEN_PATTERN = Pattern.compile("(\\d+)([smhdy]|mo)", Pattern.CASE_INSENSITIVE);

	private TimeDuration() {
		throw new UnsupportedOperationException();
	}

	@Nonnull public static String format(@Nonnull Instant instant) {
		return format(LocalDateTime.ofInstant(instant, ZoneOffset.UTC), true);
	}

	@Nonnull public static String format(@Nonnull Instant instant, boolean clampNegative) {
		return format(LocalDateTime.ofInstant(instant, ZoneOffset.UTC), LocalDateTime.now(), clampNegative);
	}

	@Nonnull public static String format(@Nonnull LocalDateTime date) {
		return format(date, true);
	}

	@Nonnull public static String format(@Nonnull LocalDateTime date, boolean clampNegative) {
		return format(date, LocalDateTime.now(), clampNegative);
	}

	@Nonnull public static String format(@Nonnull Instant i1, @Nonnull Instant i2, boolean clampNegative) {
		return format(LocalDateTime.ofInstant(i1, ZoneOffset.UTC), LocalDateTime.ofInstant(i2, ZoneOffset.UTC), clampNegative);
	}

	@Nonnull public static String format(@Nonnull LocalDateTime date1, @Nonnull LocalDateTime date2, boolean clampNegative) {
		LocalDateTime temp = LocalDateTime.from(date1);

		long years = temp.until(date2, ChronoUnit.YEARS);
		temp = temp.plusYears(years);
		long months = temp.until(date2, ChronoUnit.MONTHS);
		temp = temp.plusMonths(months);
		long days = temp.until(date2, ChronoUnit.DAYS);
		temp = temp.plusDays(days);
		long hours = temp.until(date2, ChronoUnit.HOURS);
		temp = temp.plusHours(hours);
		long minutes = temp.until(date2, ChronoUnit.MINUTES);
		temp = temp.plusMinutes(minutes);
		long seconds = temp.until(date2, ChronoUnit.SECONDS);

		if (clampNegative && (years < 0 || months < 0 || days < 0 || hours < 0 || minutes < 0 || seconds < 0))
			return "0s";

		years = Math.abs(years);
		months = Math.abs(months);
		days = Math.abs(days);
		hours = Math.abs(hours);
		minutes = Math.abs(minutes);
		seconds = Math.abs(seconds);

		StringBuilder sb = new StringBuilder();

		if (years != 0) {
			sb.append(String.format(" %dy", (int)years));
			if (months != 0)
				sb.append(String.format(" %dmo", (int)months));
		} else if (months != 0) {
			sb.append(String.format(" %dmo", (int)months));
			if (days != 0)
				sb.append(String.format(" %dd", (int)days));
		} else if (days != 0) {
			sb.append(String.format(" %dd", (int)days));
			if (hours != 0)
				sb.append(String.format(" %dh", (int)hours));
		} else if (hours != 0) {
			sb.append(String.format(" %dh", (int)hours));
			if (minutes != 0)
				sb.append(String.format(" %dm", (int)minutes));
		} else if (minutes != 0) {
			sb.append(String.format(" %dm", (int)minutes));
			if (seconds != 0)
				sb.append(String.format(" %ds", (int)seconds));
		} else {
			sb.append(String.format(" %ds", (int)seconds));
		}

		return sb.toString().substring(1);
	}

	@Nonnull public static LocalDateTime parseFuture(@Nonnull String formatted) {
		return parse(true, formatted);
	}

	@Nonnull public static LocalDateTime parsePast(@Nonnull String formatted) {
		return parse(false, formatted);
	}

	@Nonnull public static LocalDateTime parse(boolean inFuture, @Nonnull String formatted) {
		return parse(LocalDateTime.now(), inFuture, formatted);
	}

	@Nonnull public static LocalDateTime parseFuture(@Nonnull LocalDateTime base, @Nonnull String formatted) {
		return parse(base, true, formatted);
	}

	@Nonnull public static LocalDateTime parsePast(@Nonnull LocalDateTime base, @Nonnull String formatted) {
		return parse(base, false, formatted);
	}

	@Nonnull public static LocalDateTime parse(@Nonnull LocalDateTime base, boolean inFuture, @Nonnull String formatted) {
		LocalDateTime temp = LocalDateTime.from(base);

		Matcher m = TIME_DURATION_TOKEN_PATTERN.matcher(formatted);
		while (m.find()) {
			int units = Integer.parseInt(m.group(1));
			String unitType = m.group(2);
			switch (unitType.toLowerCase()) {
				case "s":
					temp = temp.plusSeconds(units * (inFuture ? 1 : -1));
					break;
				case "m":
					temp = temp.plusMinutes(units * (inFuture ? 1 : -1));
					break;
				case "h":
					temp = temp.plusHours(units * (inFuture ? 1 : -1));
					break;
				case "d":
					temp = temp.plusDays(units * (inFuture ? 1 : -1));
					break;
				case "mo":
					temp = temp.plusMonths(units * (inFuture ? 1 : -1));
					break;
				case "y":
					temp = temp.plusYears(units * (inFuture ? 1 : -1));
					break;
			}
		}

		return temp;
	}
}