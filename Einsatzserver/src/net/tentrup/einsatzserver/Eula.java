package net.tentrup.einsatzserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

public class Eula {

	// EULA_KEY should be changed with every new version of the EULA to ensure that EULA has to be accepted at program start
	public static final String EULA_KEY = "eula_1";
	private Activity m_activity;

	public Eula(Activity context) {
		m_activity = context;
	}

	private PackageInfo getPackageInfo() {
		PackageInfo pi = null;
		try {
			pi = m_activity.getPackageManager().getPackageInfo(m_activity.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi;
	}

	public void show() {
		PackageInfo versionInfo = getPackageInfo();
		final String eulaKey = EULA_KEY;
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_activity);
		boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
		if (hasBeenShown == false) {

			// Show the Eula
			String title = m_activity.getString(R.string.eula_title);

			AlertDialog.Builder builder = new AlertDialog.Builder(m_activity)
			.setTitle(title)
			.setMessage(m_activity.getString(R.string.app_name) + " v" + versionInfo.versionName + 
					System.getProperty("line.separator") +
					System.getProperty("line.separator") +
					readEula(m_activity))
			.setPositiveButton(R.string.eula_accept,
					new Dialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					// Mark this version as read.
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean(eulaKey, true);
					editor.commit();
					dialogInterface.dismiss();
				}
			})
			.setNegativeButton(R.string.eula_reject,
					new Dialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Close the activity as they have declined
					// the EULA
					m_activity.finish();
				}

			});
			builder.create().show();
		}
	}

	public static String readEula(Activity activity) {
		BufferedReader reader = null;
		try {
			InputStream stream = activity.getResources().openRawResource(R.raw.mit_license);
			reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				buffer.append(line).append(System.getProperty("line.separator"));
			}
			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException exc) {
					// Ignore
				}
			}
		}
		
	}
}
