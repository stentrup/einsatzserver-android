package net.tentrup.einsatzserver.testmode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Launches the Einsatzserver App in test mode.
 * 
 * @author Tentrup
 * 
 */
public class EinsatzserverTestmodeActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Intent launchIntent = getPackageManager().getLaunchIntentForPackage("net.tentrup.einsatzserver");
		launchIntent.putExtra("net.tentrup.einsatzserver.testmode", true);
		startActivity(launchIntent);
	}

}