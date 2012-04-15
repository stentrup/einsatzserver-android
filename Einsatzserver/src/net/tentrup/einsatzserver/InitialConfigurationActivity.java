package net.tentrup.einsatzserver;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import net.tentrup.einsatzserver.comm.Communicator;
import net.tentrup.einsatzserver.config.PreferenceKeys;
import net.tentrup.einsatzserver.model.ResultStateEnum;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Activity for initial configuration (username and password).
 * 
 * @author Tentrup
 *
 */
public class InitialConfigurationActivity extends GDActivity {

	private static final String TAG = InitialConfigurationActivity.class.getSimpleName();

	private static final int LOADING_DIALOG = 0;
	private static final int ALERT_DIALOG_LOGIN_FAILED = 4;
	private static final int ALERT_DIALOG_LOADING_ERROR = 5;
	private static final int ALERT_DIALOG_PARSE_ERROR = 6;

	private LoginTask m_task;
	private boolean m_shownDialog;

	private RecentChanges m_recentChanges;

	private Eula m_eula;

	public InitialConfigurationActivity() {
		super(ActionBar.Type.Empty);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Object retained = getLastNonConfigurationInstance();
		if (retained instanceof LoginTask) {
			Log.i(TAG, "Reclaiming previous background task.");
			m_task = (LoginTask) retained;
			m_task.setActivity(this);
		}
		// check if app was started in test mode
		boolean testmode = getIntent().getBooleanExtra(Communicator.PREF_TESTMODE, false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putBoolean(Communicator.PREF_TESTMODE, testmode);
		editor.commit();
		m_eula = new Eula(this);
		m_eula.show(new Resume() {
			@Override
			public void execute() {
				m_recentChanges = new RecentChanges(InitialConfigurationActivity.this);
				m_recentChanges.show(new Resume() {
					@Override
					public void execute() {
						if (isLoginDataConfigured()) {
							showHomeScreen();
						} else {
							setTitle(R.string.initial_configuration);
							setActionBarContentView(R.layout.initial_configuration);
							findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									performLogin();
								}
							});
						}
					}
				});
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		m_eula.dismiss();
		if (m_recentChanges != null) {
			m_recentChanges.dismiss();
		}
	}

	/**
	 * Checks if username and password are set.
	 */
	private boolean isLoginDataConfigured() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getString(PreferenceKeys.CONFIGURATION_USERNAME, "").trim().length() < 1 
				|| prefs.getString(PreferenceKeys.CONFIGURATION_PASSWORD, "").trim().length() < 1) {
			return false;
		} else {
			return true;
		}
	}

	private void showHomeScreen() {
		Intent intent = new Intent(InitialConfigurationActivity.this, HomeScreenActivity.class);
		startActivity(intent);
	}

	private void performLogin() {
		EditText usernameEditText = (EditText) findViewById(R.id.username_editText);
		EditText passwordEditText = (EditText) findViewById(R.id.password_editText);
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		Log.i(TAG, "Creating new background task.");
		m_task = new LoginTask(this, username, password);
		m_task.execute();
	}

	public void onLoginTaskCompleted(ResultStateEnum result, String username, String password) {
		if (result == ResultStateEnum.SUCCESSFUL) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Editor prefsEditor = prefs.edit();
			prefsEditor.putString(PreferenceKeys.CONFIGURATION_USERNAME, username);
			prefsEditor.putString(PreferenceKeys.CONFIGURATION_PASSWORD, password);
			prefsEditor.commit();
			showHomeScreen();
		} else {
			if (result == ResultStateEnum.LOGIN_FAILED) {
				showDialog(ALERT_DIALOG_LOGIN_FAILED);
			} else if (result == ResultStateEnum.LOADING_ERROR) {
				showDialog(ALERT_DIALOG_LOADING_ERROR);
			} else if (result == ResultStateEnum.PARSE_ERROR) {
				showDialog(ALERT_DIALOG_PARSE_ERROR);
			}
		}
		// Check added because dismissDialog throws an exception if the current
		// activity hasn't shown it. This Happens if task finishes early enough
		// before an orientation change that the dialog is already gone when
		// the previous activity bundles up the dialogs to reshow.
		if (m_shownDialog) {
			removeDialog(LOADING_DIALOG);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == LOADING_DIALOG) {
			ProgressDialog loadingDialog = new OperationsLoadingProgressDialog(this, m_task);
			loadingDialog.setMessage(getString(R.string.loading));
			loadingDialog.setIndeterminate(true);
			loadingDialog.setCancelable(true);
			return loadingDialog;
		} else if (id == ALERT_DIALOG_LOGIN_FAILED) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.alert_login_failed)
			       .setCancelable(false)
			       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
			           }
			       });
			AlertDialog alert = builder.create();
			return alert;
		} else if (id == ALERT_DIALOG_LOADING_ERROR) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.alert_loading_error)
			       .setCancelable(false)
			       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
			           }
			       });
			AlertDialog alert = builder.create();
			return alert;
		} else if (id == ALERT_DIALOG_PARSE_ERROR) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.alert_parse_error)
			       .setCancelable(false)
			       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.dismiss();
			           }
			       });
			AlertDialog alert = builder.create();
			return alert;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * After a screen orientation change, this method is invoked. As we're going
	 * to state save the task, we can no longer associate it with the Activity
	 * that is going to be destroyed here.
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		if (m_task != null) {
			m_task.setActivity(null);
		}
		return m_task;
	}

	/**
	 * Here, we're maintaining the mShownDialog flag in the activity so that it
	 * knows that the progress dialog has been shown. The flag is required when
	 * dismissing the dialog, as the only activity that is allowed to dismiss
	 * the dialog is the activity that has also created it.
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if (id == LOADING_DIALOG) {
			m_shownDialog = true;
		}
	}

	private class LoginTask extends AsyncTask<Void, Void, Void> {

		private InitialConfigurationActivity m_activity;
		private ResultStateEnum m_result;
		private boolean m_completed;
		private final String m_username, m_password;

		private LoginTask(InitialConfigurationActivity activity, String username, String password) {
			m_activity = activity;
			m_username = username;
			m_password = password;
		}

		@Override
		protected Void doInBackground(Void... params) {
			m_result = new Communicator(getApplicationContext()).login(m_username, m_password);
			return null;
		}

		@Override
		protected void onPreExecute() {
			m_activity.showDialog(LOADING_DIALOG);
		}

		/**
		 * When the task is completed, notify the Activity.
		 */
		@Override
		protected void onPostExecute(Void result) {
			m_completed = true;
			notifyActivityTaskCompleted();
		}

		private void setActivity(InitialConfigurationActivity activity) {
			m_activity = activity;
			if (m_completed) {
				notifyActivityTaskCompleted();
			}
		}

		/**
		 * Helper method to notify the activity that this task was completed.
		 */
		private void notifyActivityTaskCompleted() {
			if (null != m_activity) {
				m_activity.onLoginTaskCompleted(m_result, m_username, m_password);
			}
		}

	}
}
