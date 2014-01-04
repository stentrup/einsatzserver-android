package net.tentrup.einsatzserver.config;

import net.tentrup.einsatzserver.ConfigurationActivity;

/**
 * This class contains the keys of the preferences of the app.
 * They are partly configured in {@link ConfigurationActivity} and also defined in res/layout/configuaration.xml.
 * 
 * @author Tentrup
 *
 */
public class PreferenceKeys {

	// ConfigurationActivity
	public static final String CONFIGURATION_PASSWORD = "configuration.password";
	public static final String CONFIGURATION_USERNAME = "configuration.username";
	public static final String CONFIGURATION_UI_SHOW_REQUESTED_BOOKINGS_COUNT = "configuration.ui.showRequestedBookingsCount";
	public static final String CONFIGURATION_UI_DAY_OF_WEEK = "configuration.ui.showDayOfWeek";
	public static final String CONFIGURATION_CALENDAR_LOCATION = "configuration.calendar.location";
	public static final String CONFIGURATION_CALENDAR_STARTTIME = "configuration.calendar.starttime";
	public static final String CONFIGURATION_SHARE_MESSAGE = "configuration.share.message";

	// other preferences
	public static final String CONFIGURATION_FILTER_OCCUPIED_OPERATIONS = "configuration.filter.occupied_operations";

}
