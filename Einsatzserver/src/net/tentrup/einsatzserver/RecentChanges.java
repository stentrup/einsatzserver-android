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
import android.webkit.WebView;

/**
 * Shows recent changes as a dialog.
 * 
 * @author Tentrup
 *
 */
public class RecentChanges {

	public static final String KEY = "recent_changes_";
	private final Activity m_parent;
	private Dialog m_dialog = null;

	public RecentChanges(Activity context) {
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
	 * Show dialog with Recent Changes
	 * 
	 * @param resume a Resume which should be executed after displaying the dialog or if the dialog is not displayed.
	 */
	public void show(final Resume resume) {
		PackageInfo versionInfo = getPackageInfo();
		
		final String key = KEY + versionInfo.versionCode;
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_parent);
		boolean hasBeenShown = prefs.getBoolean(key, false);
		if (!hasBeenShown) {
			// Show recent changes
			String title = m_parent.getString(R.string.recent_changes_title);
			WebView webView = new WebView(m_parent);
			webView.loadDataWithBaseURL(null, readRecentChanges(m_parent), "text/html", "UTF-8", null);
			AlertDialog.Builder builder = new AlertDialog.Builder(m_parent)
			.setTitle(title)
			.setView(webView)
			.setCancelable(false)
			.setPositiveButton(R.string.ok,
					new Dialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					// Mark this version as read.
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean(key, true);
					editor.commit();
					dialogInterface.dismiss();
					resume.execute();
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

	public static String readRecentChanges(Activity activity) {
		BufferedReader reader = null;
		try {
			InputStream stream = activity.getResources().openRawResource(R.raw.recent_changes);
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
