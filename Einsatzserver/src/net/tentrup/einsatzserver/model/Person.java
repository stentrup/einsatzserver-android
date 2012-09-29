package net.tentrup.einsatzserver.model;

import java.io.Serializable;

import org.joda.time.LocalTime;

public class Person implements Serializable {

	private static final long serialVersionUID = 1L;

	private String m_name;
	private String m_surname;
	private BookingState m_bookingState;
	private String m_division;
	private String m_comment;
	private LocalTime m_startTime;
	private LocalTime m_endTime;
	private String m_qualification;

	public String getName() {
		return m_name;
	}
	public void setName(String name) {
		this.m_name = name;
	}
	public String getSurname() {
		return m_surname;
	}
	public void setSurname(String surname) {
		this.m_surname = surname;
	}
	public BookingState getBookingState() {
		return m_bookingState;
	}
	public void setBookingState(BookingState bookingState) {
		m_bookingState = bookingState;
	}
	public String getComment() {
		return m_comment;
	}
	public void setComment(String comment) {
		m_comment = comment;
	}
	public String getDivision() {
		return m_division;
	}
	public void setDivision(String division) {
		m_division = division;
	}
	public LocalTime getStartTime() {
		return m_startTime;
	}
	public void setStartTime(LocalTime startTime) {
		m_startTime = startTime;
	}
	public LocalTime getEndTime() {
		return m_endTime;
	}
	public void setEndTime(LocalTime endTime) {
		m_endTime = endTime;
	}
	public String getQualification() {
		return m_qualification;
	}
	public void setQualification(String qualification) {
		m_qualification = qualification;
	}
}
