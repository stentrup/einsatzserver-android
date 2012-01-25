package net.tentrup.einsatzserver.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Model class for an Operation.
 * 
 * @author Tentrup
 * 
 */
public class Operation {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");
	private static final DateTimeFormatter DATE_WITH_DAY_OF_WEEK_FORMATTER = DateTimeFormat.forPattern("EE dd.MM.yyyy");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");

	private int m_id;
	private String m_type;
	private BookingState m_bookingState;
	private LocalDate m_startDate;
	private LocalTime m_startTime;
	private String m_location;
	private String m_description;

	public int getId() {
		return m_id;
	}

	public void setId(int id) {
		m_id = id;
	}

	public String getType() {
		return m_type;
	}

	public void setType(String type) {
		m_type = type;
	}

	public BookingState getBookingState() {
		return m_bookingState;
	}

	public void setBookingState(BookingState bookingState) {
		m_bookingState = bookingState;
	}

	public LocalDate getStartDate() {
		return m_startDate;
	}

	public String getBegin(boolean includeDayOfWeek) {
		StringBuilder builder = new StringBuilder();
		builder.append(getStartDate() == null ? "" : printDate(getStartDate(), includeDayOfWeek));
		builder.append(" ");
		builder.append(getStartTime() == null ? "" : printTime(getStartTime()));
		return builder.toString().trim();
	}

	public LocalTime getStartTime() {
		return m_startTime;
	}

	public String getLocation() {
		return m_location;
	}

	public String getDescription() {
		return m_description;
	}

	public void setDate(LocalDate date) {
		m_startDate = date;
	}

	public void setStartTime(LocalTime startTime) {
		m_startTime = startTime;
	}

	public void setLocation(String location) {
		m_location = location;
	}

	public void setDescription(String description) {
		m_description = description;
	}

	@Override
	public String toString() {
		return m_startDate + " " + m_description;
	}

	public static String printDate(LocalDate localDate, boolean includeDayOfWeek) {
		DateTimeFormatter formatter = DATE_FORMATTER;
		if (includeDayOfWeek) {
			formatter = DATE_WITH_DAY_OF_WEEK_FORMATTER;
		}
		return localDate.toString(formatter);
	}

	public static String printTime(LocalTime localTime) {
		return localTime.toString(TIME_FORMATTER);
	}
}
