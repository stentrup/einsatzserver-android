package net.tentrup.einsatzserver;

import greendroid.app.GDActivity;

import java.util.List;

import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.ResultStateEnum;
import net.tentrup.einsatzserver.model.ResultWrapper;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public abstract class AbstractOperationsActivity extends GDActivity {

	private static final String TAG = AbstractOperationsActivity.class.getSimpleName();

	private static final int LOADING_DIALOG = 0;
	private static final int ALERT_DIALOG_LOGIN_FAILED = 1;
	private static final int ALERT_DIALOG_LOADING_ERROR = 2;
	private static final int ALERT_DIALOG_PARSE_ERROR = 3;
	private static final int ALERT_DIALOG_NO_OPERATIONS = 4;
	public static final String OPERATION_ID = "operationId";

	private ListRefresher m_task;
	private boolean m_shownDialog;
//	private OperationsListAdapter m_listAdapter;

	/**
	 * After a screen orientation change, we associate the current ( = newly
	 * created) activity with the restored asyncTask. On a clean activity
	 * startup, we create a new task and associate the current activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.operations_list);
//		m_listAdapter = new OperationsListAdapter(getApplicationContext());
//		setListAdapter(m_listAdapter);
//		ListView lv = getListView();
//		lv.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent intent = new Intent(AbstractOperationsActivity.this, OperationDetailsActivity.class);
//				int operationId = (int) m_listAdapter.getItemId(position);
//				Log.i(TAG, "Details for operation id " + operationId);
//				intent.putExtra(OPERATION_ID, operationId);
//				startActivity(intent);
//			}
//		});
		Object retained = getLastNonConfigurationInstance();
		if (retained instanceof ListRefresher) {
			Log.i(TAG, "Reclaiming previous background task.");
			m_task = (ListRefresher) retained;
			m_task.setActivity(this);
		} else {
			startBackgroundTask();
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
//				m_listAdapter.setItems(result.getResult());
				updateResult(result.getResult());
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

	private void updateResult(List<Operation> result) {
		TableLayout table = (TableLayout) findViewById(R.id.operations_table);
		table.removeAllViews();
		for (final Operation operation : result) {
			TextView tv;
			TableRow tr2 = (TableRow) getLayoutInflater().inflate(R.layout.operations_item, null);
			tr2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(AbstractOperationsActivity.this, OperationDetailsActivity.class);
					int operationId = operation.getId();
					Log.i(TAG, "Details for operation id " + operationId);
					intent.putExtra(OPERATION_ID, operationId);
					startActivity(intent);
				}
			});
			tv = (TextView) tr2.findViewById(R.id.cell_day_of_week);
			tv.setText(Operation.printDayOfWeek(operation.getStartDate()));
			tv = (TextView) tr2.findViewById(R.id.cell_date);
			tv.setText(Operation.printDate(operation.getStartDate(), false, false));
			//TODO personnel only on all operations page
			tv = (TextView) tr2.findViewById(R.id.cell_personnel);
			String personnelText = operation.getPersonnelBookingConfirmed() + "/" + operation.getPersonnelRequested();
			if (operation.getPersonnelBookingConfirmed() < operation.getPersonnelRequested()) {
				tv.setBackgroundResource(R.color.color_operation_red);
			} else {
				tv.setBackgroundResource(R.color.color_operation_green);
			}
			tv.setText(personnelText);
			tv = (TextView) tr2.findViewById(R.id.cell_description);
			tv.setText(operation.getDescription());
			table.addView(tr2);
			tv = new TextView(this);
			tv.setBackgroundColor(Color.parseColor("#80808080"));
			tv.setHeight(1);
			table.addView(tv);
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
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                AbstractOperationsActivity.this.finish();
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
