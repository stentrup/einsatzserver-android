package net.tentrup.einsatzserver.model;

import java.io.Serializable;

import net.tentrup.einsatzserver.R;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import android.content.Context;


/**
 * Model class for an Operation.
 * 
 * @author Tentrup
 * 
 */
public class Operation implements Serializable {

	private static final long serialVersionUID = 4470751009754229314L;

	private static final String DATE_FORMAT = "dd.MM.yy";
	private static final String DAY_OF_WEEK_PREFIX = "EE";
	private static final String DATE_FORMAT_POSTFIX = "yy";
	private static final String TIME_FORMAT = "HH:mm";

	private int m_id;
	private String m_type;
	private BookingState m_bookingState;
	private LocalDate m_startDate;
	private LocalTime m_startTime;
	private String m_location;
	private String m_description;
	private int m_personnelRequested;
	private int m_personnelBookingConfirmed;
	private int m_personnelBookingRequested;
	private LocalDateTime m_latestChangeDate;
	private String m_latestChangeAuthor;

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

	public String getBegin(Context context, boolean includeDayOfWeek) {
		StringBuilder builder = new StringBuilder();
		if (getStartDate() != null) {
			builder.append(printDate(getStartDate(), includeDayOfWeek, true));
			builder.append(" ");
		}
		if (getStartTime() != null) {
			builder.append(printTime(getStartTime()));
			builder.append(" ");
			builder.append(context.getString(R.string.operation_oclock));
		}
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

	public int getPersonnelRequested() {
		return m_personnelRequested;
	}

	public void setPersonnelRequested(int personnelRequested) {
		m_personnelRequested = personnelRequested;
	}

	public int getPersonnelBookingConfirmed() {
		return m_personnelBookingConfirmed;
	}

	public void setPersonnelBookingConfirmed(int personnelBookingConfirmed) {
		m_personnelBookingConfirmed = personnelBookingConfirmed;
	}

	public int getPersonnelBookingRequested() {
		return m_personnelBookingRequested;
	}

	public void setPersonnelBookingRequested(int personnelBookingRequested) {
		m_personnelBookingRequested = personnelBookingRequested;
	}

	public LocalDateTime getLatestChangeDate() {
		return m_latestChangeDate;
	}

	public void setLatestChangeDate(LocalDateTime latestChangeDate) {
		m_latestChangeDate = latestChangeDate;
	}

	public String getLatestChangeAuthor() {
		return m_latestChangeAuthor;
	}

	public void setLatestChangeAuthor(String latestChangeAuthor) {
		m_latestChangeAuthor = latestChangeAuthor;
	}

	public static String printDate(LocalDate localDate, boolean includeDayOfWeek, boolean longFormat) {
		String format = DATE_FORMAT;
		if (includeDayOfWeek) {
			format = DAY_OF_WEEK_PREFIX + " " + format;
		}
		if (longFormat) {
			format += DATE_FORMAT_POSTFIX;
		}
		return localDate.toString(format);
	}

	public static String printDayOfWeek(LocalDate localDate) {
		return localDate.toString(DAY_OF_WEEK_PREFIX);
	}

	public static String printTime(LocalTime localTime) {
		return localTime.toString(TIME_FORMAT);
	}

	public static String printDateTime(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return "";
		}
		return printDate(localDateTime.toLocalDate(), false, true) + " " + printTime(localDateTime.toLocalTime());
	}
}
