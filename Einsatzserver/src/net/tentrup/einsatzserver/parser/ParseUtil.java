package net.tentrup.einsatzserver.parser;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Extracted some methods used by {@link HtmlParser} and {@link ColumnDefinitions}.
 *
 * @author Tentrup
 *
 */
public class ParseUtil {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");

	public static int parseInt(String intValue) {
		if (intValue == null) {
			return 0;
		}
		return Integer.parseInt(intValue);
	}

	public static LocalDate parseDate(String dateString) {
		if (dateString == null) {
			return null;
		}
		return DATE_FORMATTER.parseLocalDate(dateString);
	}

	public static LocalTime parseTime(String timeString) {
		if (timeString == null) {
			return null;
		}
		return TIME_FORMATTER.parseLocalTime(timeString);
	}

	public static LocalDateTime parseDateTime(String dateTimeString) {
		if (dateTimeString == null) {
			return null;
		}
		return DATE_TIME_FORMATTER.parseLocalDateTime(dateTimeString);
	}

	public static String parseString(String inputString) {
		String resultText = inputString;
		resultText = inputString.replace("\u00a0", " ");
		if (resultText.trim().length() < 1) {
			return null;
		}
		return resultText;
	}
}
