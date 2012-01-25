package net.tentrup.einsatzserver;

import greendroid.app.GDActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

/**
 * Dialog which shows some information about the application.
 * 
 * @author Tentrup
 *
 */
public class InfoActivity extends GDActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.info);
		StringBuilder infoTextBuilder = new StringBuilder();
		TextView tv = (TextView) findViewById(R.id.info_username);
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = pinfo.versionName;
			infoTextBuilder.append(getString(R.string.app_name));
			infoTextBuilder.append(" v");
			infoTextBuilder.append(versionName);
			infoTextBuilder.append(System.getProperty("line.separator"));
			infoTextBuilder.append(System.getProperty("line.separator"));
			infoTextBuilder.append(Eula.readEula(this));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		tv.setText(infoTextBuilder.toString());
//		resetEulaInfo();
	}

	private String EULA_PREFIX = "eula_";
	private void resetEulaInfo() {
		PackageInfo versionInfo = getPackageInfo();

		// the eulaKey changes every time you increment the version number in
		// the AndroidManifest.xml
		final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		Editor editor = prefs.edit();
		editor.remove(eulaKey);
		editor.commit();
	}

	private PackageInfo getPackageInfo() {
		PackageInfo pi = null;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi;
	}

}
