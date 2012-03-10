package net.tentrup.einsatzserver.model;

import net.tentrup.einsatzserver.R;


public enum BookingState {

	REQUESTED("Teilnahme vorgemerkt", "vorg.", R.string.bookingstate_requested, R.string.bookingstate_requested_short),
	CONFIRMED("Teilnahme verbindlich", "geb.", R.string.bookingstate_confirmed, R.string.bookingstate_confirmed_short),
	ABSENT("Abwesend", "abw.", R.string.bookingstate_absent, R.string.bookingstate_absent_short),
	UNKNOWN("Unbekannt", "unb.", R.string.bookingstate_unknown, R.string.bookingstate_unknown_short);

	private final String m_text;
	private final String m_shortText;
	private final int m_resourceId;
	private final int m_resourceIdShort;

	BookingState(String text, String shortText, int resourceId, int resourceIdShort) {
		m_text = text;
		m_shortText = shortText;
		m_resourceId = resourceId;
		m_resourceIdShort = resourceIdShort;
	}

	public int getResourceId() {
		return m_resourceId;
	}

	public int getResourceIdShort() {
		return m_resourceIdShort;
	}

	public static BookingState parseText(String text) {
		for (BookingState bookingState : values()) {
			if (bookingState.m_text.equals(text)) {
				return bookingState;
			}
		}
		return UNKNOWN;
	}

	public static BookingState parseShortText(String shortText) {
		for (BookingState bookingState : values()) {
			if (bookingState.m_shortText.equals(shortText)) {
				return bookingState;
			}
		}
		return UNKNOWN;
	}
}
