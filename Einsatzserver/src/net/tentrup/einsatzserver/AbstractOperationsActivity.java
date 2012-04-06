package net.tentrup.einsatzserver;

import greendroid.app.GDActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.util.List;

import net.tentrup.einsatzserver.config.PreferenceKeys;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.ResultStateEnum;
import net.tentrup.einsatzserver.model.ResultWrapper;
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
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Base class for activity list.
 * 
 * @author Tentrup
 * 
 */
public abstract class AbstractOperationsActivity extends GDActivity {

	private static final String TAG = AbstractOperationsActivity.class.getSimpleName();

	private static final int LOADING_DIALOG = 0;
	private static final int ALERT_DIALOG_LOGIN_FAILED = 1;
	private static final int ALERT_DIALOG_LOADING_ERROR = 2;
	private static final int ALERT_DIALOG_PARSE_ERROR = 3;
	private static final int ALERT_DIALOG_NO_OPERATIONS = 4;
	private static final int COLUMNS_DIALOG = 5;
	public static final String OPERATION_ID = "operationId";

	private ListRefresher m_task;
	private boolean m_shownDialog;
	private List<Operation> m_result;

	/**
	 * After a screen orientation change, we associate the current ( = newly
	 * created) activity with the restored asyncTask. On a clean activity
	 * startup, we create a new task and associate the current activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.operations_list);
		addToActionBar();
		Object retained = getLastNonConfigurationInstance();
		if (retained instanceof ListRefresher) {
			Log.i(TAG, "Reclaiming previous background task.");
			m_task = (ListRefresher) retained;
			m_task.setActivity(this);
		} else {
			startBackgroundTask();
		}
	}

	protected void addToActionBar() {
		ActionBarItem filterAction = getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(new ActionBarDrawable(this, R.drawable.ic_action_bar_columns));
		addActionBarItem(filterAction, R.id.action_bar_columns);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
        case R.id.action_bar_columns:
        	showDialog(COLUMNS_DIALOG);
            return true;
        default:
            return super.onHandleActionBarItemClick(item, position);
        }
	}

	private void startBackgroundTask() {
		Log.i(TAG, "Creating new background task.");
		m_task = new ListRefresher(this);
		m_task.execute();
	}

	/**
	 * After a screen orientation change, this method is invoked. As we're going
	 * to state save the task, we can no longer associate it with the Activity
	 * that is going to be destroyed here.
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		m_task.setActivity(null);
		return m_task;
	}

	/**
	 * When the aSyncTask has notified the activity that it has completed, we
	 * can refresh the list control, and attempt to dismiss the dialog. We'll
	 * only dismiss the dialog
	 */
	private void onTaskCompleted() {
		Log.i(TAG, "Activity " + this
				+ " has been notified the task is complete.");
		ResultWrapper<List<Operation>> result = m_task.getResult();
		if (result.getState() == ResultStateEnum.SUCCESSFUL) {
			if (result.getResult().size() > 0) {
				setResult(result.getResult());
			} else {
				showDialog(ALERT_DIALOG_NO_OPERATIONS);
			}
		} else {
			if (result.getState() == ResultStateEnum.LOGIN_FAILED) {
				showDialog(ALERT_DIALOG_LOGIN_FAILED);
			} else if (result.getState() == ResultStateEnum.LOADING_ERROR) {
				showDialog(ALERT_DIALOG_LOADING_ERROR);
			} else if (result.getState() == ResultStateEnum.PARSE_ERROR) {
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

	private void setResult(List<Operation> result) {
		m_result = result;
		updateView();
	}

	/**
	 * Returns true if the given item should be shown.
	 */
	protected abstract boolean showItem(Operation operation);

	protected void updateView() {
		TableLayout table = (TableLayout) findViewById(R.id.operations_table);
		table.removeAllViews();
		for (final Operation operation : m_result) {
			if (showItem(operation)) {
				TextView textView;
				TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.operations_item, null);
				tableRow.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(AbstractOperationsActivity.this, OperationDetailsActivity.class);
						int operationId = operation.getId();
						Log.i(TAG, "Details for operation id " + operationId);
						intent.putExtra(OPERATION_ID, operationId);
						startActivity(intent);
					}
				});
				textView = (TextView) tableRow.findViewById(R.id.cell_day_of_week);
				textView.setText(Operation.printDayOfWeek(operation.getStartDate()));
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
				boolean showDayOfWeek = sharedPreferences.getBoolean(PreferenceKeys.CONFIGURATION_UI_DAY_OF_WEEK, true);
				if (!showDayOfWeek) {
					textView.setVisibility(View.GONE);
				}
				textView = (TextView) tableRow.findViewById(R.id.cell_date);
				textView.setText(Operation.printDate(operation.getStartDate(), false, false));
				textView = (TextView) tableRow.findViewById(R.id.cell_state_text);
				updateStateTextView(operation, textView);
				ImageView imageView = (ImageView) tableRow.findViewById(R.id.cell_state_image);
				updateStateImageView(operation, imageView);
				textView = (TextView) tableRow.findViewById(R.id.cell_description);
				textView.setText(operation.getDescription());
				table.addView(tableRow);
				textView = new TextView(this);
				textView.setBackgroundResource(R.color.color_hr);
				textView.setHeight(1);
				table.addView(textView);
			}
		}
	}

	/**
	 * The cell_state_text {@link TextView} is used differently in subclasses
	 */
	protected abstract void updateStateTextView(Operation operation, TextView textView);

	/**
	 * The cell_state_image {@link ImageView} is used differently in subclasses
	 */
	protected abstract void updateStateImageView(Operation operation, ImageView imageView);

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
			                AbstractOperationsActivity.this.finish();
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
			                AbstractOperationsActivity.this.finish();
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
			                AbstractOperationsActivity.this.finish();
			           }
			       });
			AlertDialog alert = builder.create();
			return alert;
		} else if (id == ALERT_DIALOG_NO_OPERATIONS) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.alert_no_operations)
			       .setCancelable(false)
			       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                AbstractOperationsActivity.this.finish();
			           }
			       });
			AlertDialog alert = builder.create();
			return alert;
		} else if (id == COLUMNS_DIALOG) {
			String[] items = new String[] {getString(R.string.configuration_ui_showRequestedBookingsCount), getString(R.string.configuration_ui_showDayOfWeek)};
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			boolean prefShowRequestedBookingsCount = prefs.getBoolean(PreferenceKeys.CONFIGURATION_UI_SHOW_REQUESTED_BOOKINGS_COUNT, false);
			boolean prefShowDayOfWeek = prefs.getBoolean(PreferenceKeys.CONFIGURATION_UI_DAY_OF_WEEK, true);
			boolean[] state = new boolean[] {prefShowRequestedBookingsCount, prefShowDayOfWeek};
			final FilterChoiceClickListener listener = new FilterChoiceClickListener(state);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.visible_columns)
			.setMultiChoiceItems(items, state, listener)
			.setCancelable(false)
			.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Editor prefEditor = prefs.edit();
					prefEditor.putBoolean(PreferenceKeys.CONFIGURATION_UI_SHOW_REQUESTED_BOOKINGS_COUNT, listener.getChecked()[0]);
					prefEditor.putBoolean(PreferenceKeys.CONFIGURATION_UI_DAY_OF_WEEK, listener.getChecked()[1]);
					prefEditor.commit();
					removeDialog(COLUMNS_DIALOG);
					updateView();
				}
			})
			.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					removeDialog(COLUMNS_DIALOG);
				}
			});
			AlertDialog alert = builder.create();
			return alert;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "Activity has been paused.");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "Activity has been resumed.");
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

	protected abstract ResultWrapper<List<Operation>> getActivityResult();

	private class ListRefresher extends AsyncTask<Void, Void, Void> {

		private AbstractOperationsActivity m_Activity;
		private ResultWrapper<List<Operation>> m_Result;
		private boolean m_Completed;

		private ListRefresher(AbstractOperationsActivity activity) {
			m_Activity = activity;
		}

		private ResultWrapper<List<Operation>> getResult() {
			return m_Result;
		}

		@Override
		protected Void doInBackground(Void... params) {
			m_Result = getActivityResult();
			return null;
		}

		@Override
		protected void onPreExecute() {
			m_Activity.showDialog(LOADING_DIALOG);
		}

		/**
		 * When the task is completed, notify the Activity.
		 */
		@Override
		protected void onPostExecute(Void result) {
			m_Completed = true;
			notifyActivityTaskCompleted();
		}

		private void setActivity(AbstractOperationsActivity activity) {
			m_Activity = activity;
			if (m_Completed) {
				notifyActivityTaskCompleted();
			}
		}

		/**
		 * Helper method to notify the activity that this task was completed.
		 */
		private void notifyActivityTaskCompleted() {
			if (null != m_Activity) {
				m_Activity.onTaskCompleted();
			}
		}

	}
}
