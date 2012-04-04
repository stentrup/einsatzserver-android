package net.tentrup.einsatzserver;

import greendroid.app.GDApplication;

/**
 * Main class of this Android application.
 * 
 * @author Tentrup
 *
 */
public class EinsatzserverApplication extends GDApplication {

	@Override
	public Class<?> getHomeActivityClass() {
		return HomeScreenActivity.class;
	}
}
