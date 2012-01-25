package net.tentrup.einsatzserver;

import android.app.Activity;

/**
 * Defines the resource id and activity class for one item of the home screen.
 * 
 * @author Tentrup
 *
 */
public class HomeScreenItem {

	private final int m_resourceId;
	private final Class<? extends Activity> m_activityClass;

	public HomeScreenItem(int resourceId, Class<? extends Activity> activityClass) {
		this.m_resourceId = resourceId;
		this.m_activityClass = activityClass;
	}

	public int getResourceId() {
		return m_resourceId;
	}

	public Class<? extends Activity> getActivityClass() {
		return m_activityClass;
	}
}
