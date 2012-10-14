package net.tentrup.einsatzserver;

import greendroid.app.GDActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.Person;

import org.joda.time.LocalTime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Activity which shows detailed information of the operations personnel.
 *
 * @author Tentrup
 *
 */
public class OperationPersonnelActivity extends GDActivity {

	private static final int COLUMNS_DIALOG = 1;

	private final SwitchableTableColumn[] columns = new SwitchableTableColumn[] {
			new SwitchableTableColumn(R.string.operation_personnel_state, R.string.operation_personnel_state_short, "configuration.ui.operation.personnel.state", true) {
				@Override
				public String getText(Person person) {
					return getString(person.getBookingState().getResourceIdShort());
				}
			},
			new SwitchableTableColumn(R.string.operation_personnel_division, R.string.operation_personnel_division_short, "configuration.ui.operation.personnel.division", true) {
				@Override
				public String getText(Person person) {
					return person.getDivision();
				}
			},
			new SwitchableTableColumn(R.string.operation_personnel_surname, R.string.operation_personnel_surname, "configuration.ui.operation.personnel.surname", true) {
				@Override
				public String getText(Person person) {
					return person.getSurname();
				}
			},
			new SwitchableTableColumn(R.string.operation_personnel_name, R.string.operation_personnel_name, "configuration.ui.operation.personnel.name", true) {
				@Override
				public String getText(Person person) {
					return person.getName();
				}
			},
			new SwitchableTableColumn(R.string.operation_personnel_qualification, R.string.operation_personnel_qualification_short, "configuration.ui.operation.personnel.qualification", true) {
				@Override
				public String getText(Person person) {
					return person.getQualification();
				}
			},
			new SwitchableTableColumn(R.string.operation_personnel_startTime, R.string.operation_personnel_startTime, "configuration.ui.operation.personnel.startTime", false) {
				@Override
				public String getText(Person person) {
					return toText(person.getStartTime());
				}
			},
			new SwitchableTableColumn(R.string.operation_personnel_endTime, R.string.operation_personnel_endTime, "configuration.ui.operation.personnel.endTime", false) {
				@Override
				public String getText(Person person) {
					return toText(person.getEndTime());
				}
			},
			new SwitchableTableColumn(R.string.operation_personnel_comment, R.string.operation_personnel_comment, "configuration.ui.operation.personnel.comment", true) {
				@Override
				public String getText(Person person) {
					return person.getComment();
				}
			}
	};

	private String toText(LocalTime time) {
		if (time != null) {
			return time.toString("HH:mm") + " " + getString(R.string.operation_oclock);
		} else {
			return "";
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.operation_personnel);
		addToActionBar();
		updateView();
	}

	private void addToActionBar() {
		ActionBarItem filterAction = getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(new ActionBarDrawable(this, R.drawable.ic_action_bar_columns));
		addActionBarItem(filterAction, R.id.action_bar_columns);
	}

	private void updateView() {
		TableLayout layout = (TableLayout) findViewById(R.id.personnelTableLayout);
		layout.removeAllViews();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		addHorizontalLine(layout);
		TableRow tableHeader = (TableRow) getLayoutInflater().inflate(R.layout.operation_personnel_table_row, null);
		layout.addView(tableHeader);
		for (SwitchableTableColumn column : columns) {
			if (sharedPreferences.getBoolean(column.getPreferenceKey(), column.getPreferenceDefaultValue())) {
				addVerticalLine(tableHeader);
				TextView headerCell = (TextView) getLayoutInflater().inflate(R.layout.operation_personnel_table_header_cell, null);
				headerCell.setText(column.getTableHeaderName(this));
				tableHeader.addView(headerCell);
			}
		}
		addVerticalLine(tableHeader);

		OperationDetails details = (OperationDetails) getIntent().getSerializableExtra(OperationDetailsActivity.OPERATION_DETAILS);
		for (Person person : details.getPersonnel()) {
			addHorizontalLine(layout);
			TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.operation_personnel_table_row, null);
			layout.addView(tableRow);
			for (SwitchableTableColumn column : columns) {
				if (sharedPreferences.getBoolean(column.getPreferenceKey(), column.getPreferenceDefaultValue())) {
					addVerticalLine(tableRow);
					TextView cell = (TextView) getLayoutInflater().inflate(R.layout.operation_personnel_table_cell, null);
					cell.setText(column.getText(person));
					tableRow.addView(cell);
				}
			}
			addVerticalLine(tableRow);
		}
		addHorizontalLine(layout);
	}

	private void addHorizontalLine(ViewGroup parent) {
		getLayoutInflater().inflate(R.layout.table_horizontal_line, parent);
	}

	private void addVerticalLine(ViewGroup parent) {
		getLayoutInflater().inflate(R.layout.table_vertical_line, parent);
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

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == COLUMNS_DIALOG) {
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			boolean[] state = new boolean[columns.length];
			String[] items = new String[columns.length];
			for (int i = 0; i < columns.length; i++) {
				state[i] = prefs.getBoolean(columns[i].getPreferenceKey(), columns[i].getPreferenceDefaultValue());
				items[i] = columns[i].getDialogName(this);
			}
			final FilterChoiceClickListener listener = new FilterChoiceClickListener(state);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.visible_columns)
			.setMultiChoiceItems(items, state, listener)
			.setCancelable(false)
			.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Editor prefEditor = prefs.edit();
					for (int i = 0; i < columns.length; i++) {
						prefEditor.putBoolean(columns[i].getPreferenceKey(), listener.getChecked()[i]);
					}
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
}
