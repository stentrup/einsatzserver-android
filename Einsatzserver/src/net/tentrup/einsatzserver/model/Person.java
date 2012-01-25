package net.tentrup.einsatzserver.model;

public class Person {

	private String m_name;
	private String m_surname;
	private BookingState m_bookingState;

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

}
