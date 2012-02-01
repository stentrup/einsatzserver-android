package net.tentrup.einsatzserver;

import greendroid.app.GDActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.util.List;

import net.tentrup.einsatzserver.comm.Communicator;
import net.tentrup.einsatzserver.config.PreferenceKeys;
import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.Person;
import net.tentrup.einsatzserver.model.ResultStateEnum;
import net.tentrup.einsatzserver.model.ResultWrapper;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class OperationDetailsActivity extends GDActivity {

	private static final String TAG = OperationDetailsActivity.class.getSimpleName();

	private static final int LOADING_PROGRESS_DIALOG = 1;
	private static final int BOOKING_PROGRESS_DIALOG = 2;
	private static final int ALERT_DIALOG_CALENDAR_ENTRY= 3;
	private static final int ALERT_DIALOG_LOGIN_FAILED = 4;
	private static final int ALERT_DIALOG_LOADING_ERROR = 5;
	private static final int ALERT_DIALOG_PARSE_ERROR = 6;
	private static final int ALERT_DIALOG_BOOKING_FAILED = 7;

	private ActivityTask m_task;
	private int m_dialogShown;
	private int m_operationId;
	private OperationDetails m_operationDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_operationId = getIntent().getIntExtra(AbstractOperationsActivity.OPERATION_ID, -1);
		setActionBarContentView(R.layout.operation_details);
		ScrollView scrollView = (ScrollView) findViewById(R.id.operation_details_scrollview);
		scrollView.setVisibility(View.GONE);
        addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(new ActionBarDrawable(this, R.drawable.ic_action_bar_checkmark)), R.id.action_bar_check);
        addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(new ActionBarDrawable(this, R.drawable.ic_action_bar_calendar)), R.id.action_bar_calendar);
		Object retained = getLastNonConfigurationInstance();
		if (retained instanceof ActivityTask) {
			Log.i(TAG, "Reclaiming previous background task.");
			m_task = (ActivityTask) retained;
			m_task.setActivity(this);
		} else {
			startLoadingTask();
		}
	}

	private void startLoadingTask() {
		Log.i(TAG, "Creating new background task.");
		m_task = new LoadingTask(this, m_operationId);
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
	private void onLoadingTaskCompleted(ResultWrapper<OperationDetails> result) {
		Log.i(TAG, "Activity " + this + " has been notified the loading task is complete.");
		// Check added because dismissDialog throws an exception if the current
		// activity hasn't shown it. This Happens if task finishes early enough
		// before an orientation change that the dialog is already gone when
		// the previous activity bundles up the dialogs to reshow.
		if (m_dialogShown == LOADING_PROGRESS_DIALOG) {
			removeDialog(LOADING_PROGRESS_DIALOG);
		}
		if (result.getState() == ResultStateEnum.SUCCESSFUL) {
			m_operationDetails = result.getResult();
			TextView tvDescription = (TextView) findViewById(R.id.operation_details_description_text);
			tvDescription.setText(m_operationDetails.getDescription());
			TextView tvLocation = (TextView) findViewById(R.id.operation_details_location_text);
			tvLocation.setText(m_operationDetails.getLocation());
			TextView tvBegin = (TextView) findViewById(R.id.operation_details_begin_text);
			tvBegin.setText(m_operationDetails.getBegin(true));
			TextView tvEnd = (TextView) findViewById(R.id.operation_details_end_text);
			tvEnd.setText(m_operationDetails.getEnd(true));
			TextView tvReportLocation = (TextView) findViewById(R.id.operation_details_report_location_text);
			tvReportLocation.setText(m_operationDetails.getReportLocation());
			TextView tvReportTime = (TextView) findViewById(R.id.operation_details_report_time_text);
			tvReportTime.setText(m_operationDetails.getReportDateComplete(true));
			TextView tvComment = (TextView) findViewById(R.id.operation_details_comment_text);
			String comment = m_operationDetails.getComment();
			if (comment != null) {
				tvComment.setText(comment);
			} else {
				TableRow trComment = (TableRow) findViewById(R.id.operation_details_comment_row);
				trComment.setVisibility(View.GONE);
			}
			TextView tvPersonnelRequested = (TextView) findViewById(R.id.operation_details_personnel_requested_text);
			tvPersonnelRequested.setText(""  + m_operationDetails.getPersonnelRequested());
			TextView tvPersonnel = (TextView) findViewById(R.id.operation_details_personnel_text);
			tvPersonnel.setText(toText(m_operationDetails.getPersonnel()));
			TextView tvCatering = (TextView) findViewById(R.id.operation_details_catering_text);
			tvCatering.setText(toText(m_operationDetails.isCatering()));
			ScrollView scrollView = (ScrollView) findViewById(R.id.operation_details_scrollview);
			scrollView.setVisibility(View.VISIBLE);
		} else {
			if (result.getState() == ResultStateEnum.LOGIN_FAILED) {
				showDialog(ALERT_DIALOG_LOGIN_FAILED);
			} else if (result.getState() == ResultStateEnum.LOADING_ERROR) {
				showDialog(ALERT_DIALOG_LOADING_ERROR);
			} else if (result.getState() == ResultStateEnum.PARSE_ERROR) {
				showDialog(ALERT_DIALOG_PARSE_ERROR);
			}
		}
	}

	private void onBookingTaskCompleted(boolean result) {
		Log.i(TAG, "Activity " + this + " has been notified the booking task is complete.");
		if (m_dialogShown == BOOKING_PROGRESS_DIALOG) {
			removeDialog(BOOKING_PROGRESS_DIALOG);
		}
		if (result) {
			Toast.makeText(getApplicationContext(), R.string.booking_successful, Toast.LENGTH_LONG).show();
			startLoadingTask(); //refresh view
		} else {
			showDialog(ALERT_DIALOG_BOOKING_FAILED);
		}
	}

	private String toText(List<Person> personnel) {
		StringBuilder builder = new StringBuilder();
		for (Person person : personnel) {
			builder.append(person.getName()).append(", ").append(person.getSurname());
			builder.append(" (").append(getString(person.getBookingState().getResourceId())).append(")");
			builder.append(System.getProperty("line.separator"));
		}
		return builder.substring(0, Math.max(builder.length() - System.getProperty("line.separator").length(), 0));
	}

	private CharSequence toText(boolean bool) {
		if (bool) {
			return getResources().getText(R.string.operation_catering_true);
		} else {
			return getResources().getText(R.string.operation_catering_false);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == LOADING_PROGRESS_DIALOG) {
			ProgressDialog loadingDialog = new OperationsLoadingProgressDialog(this, m_task);
			loadingDialog.setMessage(getString(R.string.loading));
			loadingDialog.setIndeterminate(true);
			loadingDialog.setCancelable(true);
			return loadingDialog;
		} else if (id == BOOKING_PROGRESS_DIALOG) {
			ProgressDialog loadingDialog = new AsyncTaskProgressDialog(this, m_task);
			loadingDialog.setMessage(getString(R.string.booking_progress));
			loadingDialog.setIndeterminate(true);
			loadingDialog.setCancelable(true);
			return loadingDialog;
		} else if (id == ALERT_DIALOG_CALENDAR_ENTRY) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.details_calendar_error)
			       .setCancelable(false)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
			           }
			       });
			AlertDialog alert = builder.create();
			return alert;
		} else if (id == ALERT_DIALOG_LOGIN_FAILED) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.alert_login_failed)
			       .setCancelable(false)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                OperationDetailsActivity.this.finish();
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
			        	   OperationDetailsActivity.this.finish();
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
			        	   OperationDetailsActivity.this.finish();
			           }
			       });
			AlertDialog alert = builder.create();
			return alert;
		} else if (id == ALERT_DIALOG_BOOKING_FAILED) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.alert_booking_failed)
			       .setCancelable(false)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
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
		Log.i(TAG, "onPrepareDialog");
		if (id == LOADING_PROGRESS_DIALOG) {
			Log.i(TAG, "Loading progress dialog has been prepared.");
			m_dialogShown = LOADING_PROGRESS_DIALOG;
		} else if (id == BOOKING_PROGRESS_DIALOG) {
			Log.i(TAG, "Booking progress dialog has been prepared.");
			m_dialogShown = BOOKING_PROGRESS_DIALOG;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.operation_details_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.add_to_calendar) {
			addToCalendar(m_operationDetails);
			return true;
		} else if (item.getItemId() == R.id.book) {
			book(m_operationDetails);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
        case R.id.action_bar_calendar:
        	addToCalendar(m_operationDetails);
            return true;
        case R.id.action_bar_check:
        	book(m_operationDetails);
        	return true;
        default:
            return super.onHandleActionBarItemClick(item, position);
        }
	}

	private void addToCalendar(OperationDetails operationDetails) {
		Log.i(TAG, "Add to calendar.");
		String title = operationDetails.getDescription();
		String location = getLocation(operationDetails);
		String description = getDescription(operationDetails);
		long startDate = parseDate(getStartDate(operationDetails), getStartTime(operationDetails));
		long endDate = parseDate(operationDetails.getEndDate(), operationDetails.getEndTime());
		addToCalendarWithActivity(title, description, location, startDate, endDate);
	}

	private LocalDate getStartDate(OperationDetails operationDetails) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String starttime = prefs.getString(PreferenceKeys.CONFIGURATION_CALENDAR_STARTTIME, "report");
		if ("operation".equals(starttime)) {
			return operationDetails.getStartDate();
		} else {
			return operationDetails.getReportDate();
		}
	}

	private LocalTime getStartTime(OperationDetails operationDetails) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String starttime = prefs.getString(PreferenceKeys.CONFIGURATION_CALENDAR_STARTTIME, "report");
		if ("operation".equals(starttime)) {
			return operationDetails.getStartTime();
		} else {
			return operationDetails.getReportTime();
		}
	}

	private String getDescription(OperationDetails operationDetails) {
		StringBuilder result = new StringBuilder();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String location = prefs.getString(PreferenceKeys.CONFIGURATION_CALENDAR_LOCATION, "report");
		if ("operation".equals(location)) {
			result.append(getText(R.string.operation_report_location)).append(": ").append(operationDetails.getReportLocation());
		} else {
			result.append(getText(R.string.operation_location)).append(": ").append(operationDetails.getLocation());
		}
		String starttime = prefs.getString(PreferenceKeys.CONFIGURATION_CALENDAR_STARTTIME, "report");
		result.append(", ");
		if ("operation".equals(starttime)) {
			result.append(getText(R.string.operation_report_time)).append(": ").append(operationDetails.getReportDateComplete(false));
		} else {
			result.append(getText(R.string.operation_begin)).append(": ").append(operationDetails.getBegin(false));
		}
		return result.toString();
	}

	private String getLocation(OperationDetails operationDetails) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String location = prefs.getString(PreferenceKeys.CONFIGURATION_CALENDAR_LOCATION, "report");
		if ("operation".equals(location)) {
			return operationDetails.getLocation();
		} else {
			return operationDetails.getReportLocation();
		}
	}

	private long parseDate(LocalDate date, LocalTime time) {
		if (time != null) {
			return date.toDateTime(time).toDate().getTime();
		}
		return date.toDate().getTime();
	}

	private void addToCalendarWithActivity(String title, String description, String location, long eventStart, long eventEnd) {
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("title", title);
		intent.putExtra("description", description);
		intent.putExtra("eventLocation", location);
		intent.putExtra("beginTime", eventStart);
		intent.putExtra("endTime", eventEnd);
		// Logging
		Log.i(TAG, "title: " + title);
		Log.i(TAG, "description: " + description);
		Log.i(TAG, "eventLocation: " + location);
		Log.i(TAG, "beginTime: " + eventStart);
		Log.i(TAG, "endTime: " + eventEnd);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException exc) {
			showDialog(ALERT_DIALOG_CALENDAR_ENTRY);
		}
	}

	private void book(OperationDetails operationDetails) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.booking_title);
		alert.setMessage(R.string.booking_description);

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(40);
		input.setFilters(filters);
		alert.setView(input);

		alert.setPositiveButton(R.string.booking_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Editable value = input.getText();
				String comment = value.toString();
				m_task = new BookingTask(OperationDetailsActivity.this, m_operationId, comment);
				m_task.execute();
			}
		});

		alert.setNegativeButton(R.string.booking_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}

	private abstract class ActivityTask extends AsyncTask<Void, Void, Void> {
		protected abstract void setActivity(OperationDetailsActivity activity);
	}

	private class LoadingTask extends ActivityTask {

		private OperationDetailsActivity m_activity;
		private ResultWrapper<OperationDetails> m_result;
		private boolean m_completed;
		private final int m_operationId;

		private LoadingTask(OperationDetailsActivity activity, int operationId) {
			m_activity = activity;
			m_operationId = operationId;
		}

		@Override
		protected Void doInBackground(Void... params) {
			m_result = new Communicator(getApplicationContext()).getOperationDetails(m_operationId);
			return null;
		}

		@Override
		protected void onPreExecute() {
			m_activity.showDialog(LOADING_PROGRESS_DIALOG);
		}

		/**
		 * When the task is completed, notify the Activity.
		 */
		@Override
		protected void onPostExecute(Void result) {
			m_completed = true;
			notifyActivityTaskCompleted();
		}

		protected void setActivity(OperationDetailsActivity activity) {
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
				m_activity.onLoadingTaskCompleted(m_result);
			}
		}
	}
	
	private class BookingTask extends ActivityTask {

		private OperationDetailsActivity m_activity;
		private final int m_operationId;
		private final String m_comment;
		private boolean m_result;

		public BookingTask(OperationDetailsActivity activity, int operationId, String comment) {
			m_activity = activity;
			m_operationId = operationId;
			m_comment = comment;
		}

		@Override
		protected void setActivity(OperationDetailsActivity activity) {
			m_activity = activity;
		}

		@Override
		protected Void doInBackground(Void... params) {
			m_result = new Communicator(getApplicationContext()).executeBooking(m_operationId, m_comment);
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			m_activity.showDialog(BOOKING_PROGRESS_DIALOG);
		}
	
		@Override
		protected void onPostExecute(Void result) {
			m_activity.onBookingTaskCompleted(m_result);
		}
		
	}
}
