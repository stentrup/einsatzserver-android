package net.tentrup.einsatzserver.model;

import net.tentrup.einsatzserver.R;


public enum BookingState {

	REQUESTED("Teilnahme vorgemerkt", "vorg.", R.string.bookingstate_requested),
	CONFIRMED("Teilnahme verbindlich", "geb.", R.string.bookingstate_confirmed);

	private final String m_text;
	private final String m_shortText;
	private final int m_resourceId;

	BookingState(String text, String shortText, int resourceId) {
		m_text = text;
		m_shortText = shortText;
		m_resourceId = resourceId;
	}

	public int getResourceId() {
		return m_resourceId;
	}

	public static BookingState parseText(String text) {
		for (BookingState bookingState : values()) {
			if (bookingState.m_text.equals(text)) {
				return bookingState;
			}
		}
		return null;
	}

	public static BookingState parseShortText(String shortText) {
		for (BookingState bookingState : values()) {
			if (bookingState.m_shortText.equals(shortText)) {
				return bookingState;
			}
		}
		return null;
	}
}
