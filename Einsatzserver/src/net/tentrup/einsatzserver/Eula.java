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

/**
 * Shows the EULA using a dialog.
 *
 * @author Tentrup
 *
 */
public class Eula {

	// EULA_KEY should be changed with every new version of the EULA to ensure that EULA has to be accepted at program start
	public static final String EULA_KEY = "eula_1";
	private final Activity m_parent;
	private Dialog m_dialog = null;

	public Eula(Activity context) {
		m_parent = context;
	}

	private PackageInfo getPackageInfo() {
		PackageInfo pi = null;
		try {
			pi = m_parent.getPackageManager().getPackageInfo(m_parent.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi;
	}

	/**
	 * Show dialog with EULA
	 * 
	 * @param resume a Resume which should be executed after displaying the dialog or if the dialog is not displayed.
	 */
	public void show(final Resume resume) {
		PackageInfo versionInfo = getPackageInfo();
		final String eulaKey = EULA_KEY;
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_parent);
		boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
		if (!hasBeenShown) {

			// Show the Eula
			String title = m_parent.getString(R.string.eula_title);

			AlertDialog.Builder builder = new AlertDialog.Builder(m_parent)
			.setTitle(title)
			.setMessage(m_parent.getString(R.string.app_name) + " v" + versionInfo.versionName + 
					System.getProperty("line.separator") +
					System.getProperty("line.separator") +
					readEula(m_parent))
			.setCancelable(false)
			.setPositiveButton(R.string.eula_accept,
					new Dialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					// Mark this version as read.
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean(eulaKey, true);
					editor.commit();
					dialogInterface.dismiss();
					resume.execute();
				}
			})
			.setNegativeButton(R.string.eula_reject,
					new Dialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Close the activity as they have declined
					// the EULA
					m_parent.finish();
				}

			});
			m_dialog = builder.create();
			m_dialog.show();
		} else {
			resume.execute();
		}
	}

	public void dismiss() {
		if (m_dialog != null) {
			m_dialog.dismiss();
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
