package net.tentrup.einsatzserver;

import greendroid.app.GDActivity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
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
	}

}
