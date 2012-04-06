package net.tentrup.einsatzserver;

import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.util.List;

import net.tentrup.einsatzserver.comm.Communicator;
import net.tentrup.einsatzserver.config.PreferenceKeys;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.ResultWrapper;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Activity showing all operations.
 * 
 * @author Tentrup
 *
 */
public class AllOperationsActivity extends AbstractOperationsActivity {

	private static final int FILTER_DIALOG = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateTitle();
	}

	@Override
	protected ResultWrapper<List<Operation>> getActivityResult() {
		return new Communicator(getApplicationContext()).getAllOperations();
	}

	@Override
	protected void updateStateTextView(Operation operation, TextView textView) {
		StringBuilder personnelTextBuilder = new StringBuilder();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean showRequestedBookingsCount = sharedPreferences.getBoolean(PreferenceKeys.CONFIGURATION_UI_SHOW_REQUESTED_BOOKINGS_COUNT, false);
		if (showRequestedBookingsCount && operation.getPersonnelBookingRequested() > 0) {
			personnelTextBuilder.append("(").append(operation.getPersonnelBookingRequested()).append(") ");
		}
		personnelTextBuilder.append(operation.getPersonnelBookingConfirmed());
		personnelTextBuilder.append("/");
		personnelTextBuilder.append(operation.getPersonnelRequested());
		if (operation.getPersonnelBookingConfirmed() < operation.getPersonnelRequested()) {
			textView.setBackgroundResource(R.color.color_operation_red);
		} else {
			textView.setBackgroundResource(R.color.color_operation_green);
		}
		textView.setText(personnelTextBuilder.toString());
	}

	@Override
	protected void updateStateImageView(Operation operation, ImageView imageView) {
		imageView.setVisibility(View.GONE);
	}

	@Override
	protected void addToActionBar() {
		super.addToActionBar();
		ActionBarItem filterAction = getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(new ActionBarDrawable(this, R.drawable.ic_action_bar_filter));
		addActionBarItem(filterAction, R.id.action_bar_filter);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
        case R.id.action_bar_filter:
        	showDialog(FILTER_DIALOG);
            return true;
        default:
            return super.onHandleActionBarItemClick(item, position);
        }
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == FILTER_DIALOG) {
			String[] filterItems = new String[] {getString(R.string.filtering_filter_occupied_operations)};
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			boolean prefFilterOccupiedOperations = prefs.getBoolean(PreferenceKeys.CONFIGURATION_FILTER_OCCUPIED_OPERATIONS, false);
			boolean[] filterState = new boolean[] {prefFilterOccupiedOperations};
			final FilterChoiceClickListener listener = new FilterChoiceClickListener(filterState);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.filtering_options)
			.setMultiChoiceItems(filterItems, filterState, listener)
			.setCancelable(false)
			.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Editor prefEditor = prefs.edit();
					prefEditor.putBoolean(PreferenceKeys.CONFIGURATION_FILTER_OCCUPIED_OPERATIONS, listener.getChecked()[0]);
					prefEditor.commit();
					removeDialog(FILTER_DIALOG);
					updateTitle();
					updateView();
				}
			})
			.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					removeDialog(FILTER_DIALOG);
				}
			});
			AlertDialog alert = builder.create();
			return alert;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected boolean showItem(Operation operation) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean prefFilterOccupiedOperations = prefs.getBoolean(PreferenceKeys.CONFIGURATION_FILTER_OCCUPIED_OPERATIONS, false);
		if (operation.getPersonnelBookingConfirmed() < operation.getPersonnelRequested() || !prefFilterOccupiedOperations) {
			return true;
		} else {
			return false;
		}
	}

	private void updateTitle() {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean prefFilterOccupiedOperations = prefs.getBoolean(PreferenceKeys.CONFIGURATION_FILTER_OCCUPIED_OPERATIONS, false);
		String title = getString(R.string.all_title);
		if (prefFilterOccupiedOperations) {
			title += " (" + getString(R.string.filtering_filtered) + ")";
		}
		setTitle(title);
	}

	@Override
	protected String[] getSwitchableColumnNames() {
		return new String[] {getString(R.string.configuration_ui_showRequestedBookingsCount), getString(R.string.configuration_ui_showDayOfWeek)};
	}

	@Override
	protected String[] getSwitchableColumnPreferenceKeys() {
		return new String[] {PreferenceKeys.CONFIGURATION_UI_SHOW_REQUESTED_BOOKINGS_COUNT, PreferenceKeys.CONFIGURATION_UI_DAY_OF_WEEK};
	}

	@Override
	protected boolean[] getSwitchableColumnPreferenceDefaultValues() {
		return new boolean[] {false, true};
	}
}
