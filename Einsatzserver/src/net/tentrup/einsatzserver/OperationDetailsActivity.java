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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
	private ActionBarItem m_calendarAction;
	private ActionBarItem m_bookingAction;
	private int m_dialogShown;
	private int m_operationId;
	private ResultWrapper<OperationDetails> m_resultWrapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_operationId = getIntent().getIntExtra(AbstractOperationsActivity.OPERATION_ID, -1);
		setActionBarContentView(R.layout.operation_details);
		ScrollView scrollView = (ScrollView) findViewById(R.id.operation_details_scrollview);
		scrollView.setVisibility(View.GONE);
		m_bookingAction = getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(new ActionBarDrawable(this, R.drawable.ic_action_bar_checkmark));
		m_calendarAction = getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(new ActionBarDrawable(this, R.drawable.ic_action_bar_calendar));
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
			setOperationDetails(result);
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

	private void onBookingTaskCompleted(ResultWrapper<OperationDetails> result) {
		Log.i(TAG, "Activity " + this + " has been notified the booking task is complete.");
		if (m_dialogShown == BOOKING_PROGRESS_DIALOG) {
			removeDialog(BOOKING_PROGRESS_DIALOG);
		}
		if (result.getState() == ResultStateEnum.SUCCESSFUL) {
			Toast.makeText(getApplicationContext(), R.string.booking_successful, Toast.LENGTH_LONG).show();
			setOperationDetails(result);
		} else {
			showDialog(ALERT_DIALOG_BOOKING_FAILED);
		}
	}

	private void setOperationDetails(final ResultWrapper<OperationDetails> result) {
		m_resultWrapper = result;
		OperationDetails operationDetails = result.getResult();
		LinearLayout layout = (LinearLayout) findViewById(R.id.operation_details_layout);
		addDetailsItem(layout, R.string.operation_description, operationDetails.getDescription(), false);
		addDetailsItem(layout, R.string.operation_location, operationDetails.getLocation(), true);
		addDetailsItem(layout, R.string.operation_begin, operationDetails.getBegin(this, true), false);
		addDetailsItem(layout, R.string.operation_end, operationDetails.getEnd(this, true), false);
		addDetailsItem(layout, R.string.operation_report_location, operationDetails.getReportLocation(), true);
		addDetailsItem(layout, R.string.operation_report_time, operationDetails.getReportDateComplete(this, true), false);
		if (operationDetails.getComment() != null) {
			addDetailsItem(layout, R.string.operation_comment, operationDetails.getComment(), false);
		}
		addDetailsItem(layout, R.string.operation_personnel_requested, "" + operationDetails.getPersonnelRequested(), false);
		addDetailsItem(layout, R.string.operation_personnel_count, "" + operationDetails.getPersonnelBookingConfirmed(), false);
		addDetailsItem(layout, R.string.operation_personnel, toText(operationDetails.getPersonnel()), false);
		addDetailsItem(layout, R.string.operation_catering, toText(operationDetails.isCatering()), false);
		ScrollView scrollView = (ScrollView) findViewById(R.id.operation_details_scrollview);
		scrollView.setVisibility(View.VISIBLE);
		// Populate action bar
		getActionBar().removeItem(m_bookingAction);
		getActionBar().removeItem(m_calendarAction);
		if (!m_resultWrapper.getResult().isInPersonnel(m_resultWrapper.getUsername())) {
			addActionBarItem(m_bookingAction, R.id.action_bar_check);
		}
		addActionBarItem(m_calendarAction, R.id.action_bar_calendar);
	}

	private void addDetailsItem(ViewGroup parent, int labelResourceId, final String text, boolean includeMapsLink) {
		RelativeLayout itemLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.operation_details_item, null);
		parent.addView(itemLayout);
		TextView labelView = (TextView) itemLayout.findViewById(R.id.operation_details_item_label);
		labelView.setText(labelResourceId);
		TextView textView = (TextView) itemLayout.findViewById(R.id.operation_details_item_text);
		textView.setText(text);
		ImageButton linkButton = (ImageButton) itemLayout.findViewById(R.id.operation_details_item_image);
		if (includeMapsLink) {
			linkButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showOnMap(text);
				}
			});
		} else {
			linkButton.setVisibility(View.GONE);
		}
	}

	private void showOnMap(String location) {
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + location));
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException exc) {
			//TODO: Show error message (dialog)
			Toast.makeText(this, "No Activity for map", Toast.LENGTH_LONG).show();
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

	private String toText(boolean bool) {
		if (bool) {
			return getString(R.string.operation_catering_true);
		} else {
			return getString(R.string.operation_catering_false);
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
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
        case R.id.action_bar_calendar:
        	addToCalendar();
            return true;
        case R.id.action_bar_check:
        	book();
        	return true;
        default:
            return super.onHandleActionBarItemClick(item, position);
        }
	}

	private void addToCalendar() {
		Log.i(TAG, "Add to calendar.");
		OperationDetails operationDetails = m_resultWrapper.getResult();
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
		if (operationDetails.getStartDate() == null) {
			return operationDetails.getReportDate();
		}
		if (operationDetails.getReportDate() == null) {
			return operationDetails.getStartDate();
		}
		if ("operation".equals(starttime)) {
			return operationDetails.getStartDate();
		} else {
			return operationDetails.getReportDate();
		}
	}

	private LocalTime getStartTime(OperationDetails operationDetails) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String starttime = prefs.getString(PreferenceKeys.CONFIGURATION_CALENDAR_STARTTIME, "report");
		if (operationDetails.getStartTime() == null) {
			return operationDetails.getReportTime();
		}
		if (operationDetails.getReportTime() == null) {
			return operationDetails.getStartTime();
		}
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
			result.append(getText(R.string.operation_report_time)).append(": ").append(operationDetails.getReportDateComplete(this, false));
		} else {
			result.append(getText(R.string.operation_begin)).append(": ").append(operationDetails.getBegin(this, false));
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
		if (date == null) {
			date = new LocalDate();
		}
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

	private void book() {
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
		private ResultWrapper<OperationDetails> m_result;

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
