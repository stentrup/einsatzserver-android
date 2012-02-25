package net.tentrup.einsatzserver.model;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Model class for operation details.
 * 
 * @author Tentrup
 * 
 */
public class OperationDetails extends Operation {

	private LocalDate m_endDate;
	private LocalTime m_endTime;
	private String m_reportLocation;
	private LocalDate m_reportDate;
	private LocalTime m_reportTime;
	private boolean m_catering;
	private String m_comment;
	private int m_personnelRequested;
	private List<Person> m_personnel;

	public LocalDate getEndDate() {
		return m_endDate;
	}

	public void setEndDate(LocalDate endDate) {
		m_endDate = endDate;
	}

	public LocalTime getEndTime() {
		return m_endTime;
	}

	public void setEndTime(LocalTime endTime) {
		m_endTime = endTime;
	}

	public String getEnd(boolean includeDayOfWeek) {
		StringBuilder builder = new StringBuilder();
		builder.append(getEndDate() == null ? "" : printDate(getEndDate(), includeDayOfWeek, true));
		builder.append(" ");
		builder.append(getEndTime() == null ? "" : printTime(getEndTime()));
		return builder.toString().trim();
	}

	public String getReportLocation() {
		return m_reportLocation;
	}

	public void setReportLocation(String reportLocation) {
		m_reportLocation = reportLocation;
	}

	public LocalDate getReportDate() {
		return m_reportDate;
	}

	public void setReportDate(LocalDate reportDate) {
		m_reportDate = reportDate;
	}

	public LocalTime getReportTime() {
		return m_reportTime;
	}

	public void setReportTime(LocalTime reportTime) {
		m_reportTime = reportTime;
	}

	public boolean isCatering() {
		return m_catering;
	}

	public void setCatering(boolean catering) {
		m_catering = catering;
	}

	public String getComment() {
		return m_comment;
	}

	public void setComment(String comment) {
		m_comment = comment;
	}

	public int getPersonnelRequested() {
		return m_personnelRequested;
	}

	public void setPersonnelRequested(int personnelRequested) {
		m_personnelRequested = personnelRequested;
	}

	public List<Person> getPersonnel() {
		return m_personnel;
	}

	public void setPersonnel(List<Person> personnel) {
		m_personnel = personnel;
	}

	public String getReportDateComplete(boolean includeDayOfWeek) {
		StringBuilder builder = new StringBuilder();
		builder.append(getReportDate() == null ? "" : printDate(getReportDate(), includeDayOfWeek, true));
		builder.append(" ");
		builder.append(getReportTime() == null ? "" : printTime(getReportTime()));
		return builder.toString().trim();
	}

	@Override
	public String toString() {
		StringBuilder textBuilder = new StringBuilder();
		textBuilder.append(getStartDate()).append(" ");
		if (getStartTime() != null) {
			textBuilder.append(getStartTime()).append(" - ");
		}
		if (getEndDate() != null && !getEndDate().equals(getStartDate())) {
			textBuilder.append(getEndDate()).append(" ");
		}
		if (getEndTime() != null) {
			textBuilder.append(getEndTime());
		}
		textBuilder.append("\n");
		textBuilder.append(getDescription()).append("\n");
		textBuilder.append(getLocation()).append("\n");
		return textBuilder.toString(); 
	}

	public boolean isInPersonnel(String aName) {
		if (aName == null) {
			return false;
		}
		for (Person person : m_personnel) {
			if (aName.equals(person.getName() + ", " + person.getSurname())) {
				return true;
			}
		}
		return false;
	}
}
