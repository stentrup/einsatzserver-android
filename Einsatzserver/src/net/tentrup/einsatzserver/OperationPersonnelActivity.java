package net.tentrup.einsatzserver;

import greendroid.app.GDActivity;
import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.Person;
import android.os.Bundle;
import android.view.View;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OperationDetails details = (OperationDetails) getIntent().getSerializableExtra(OperationDetailsActivity.OPERATION_DETAILS);
		setActionBarContentView(R.layout.operation_personnel);
		TableLayout layout = (TableLayout) findViewById(R.id.personnelTableLayout);
		for (Person person : details.getPersonnel()) {
			TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.operation_personnel_table_row, null);
			layout.addView(tableRow);
			setTextForView(tableRow, person.getDivision(), R.id.person_division);
			setTextForView(tableRow, person.getName() + ", " + person.getSurname(), R.id.person_name);
			setTextForView(tableRow, getString(person.getBookingState().getResourceId()), R.id.person_state);
			setTextForView(tableRow, person.getComment(), R.id.person_comment);
		}
	}

	private void setTextForView(View parent, String text, int viewResourceId) {
		TextView textView = (TextView) parent.findViewById(viewResourceId);
		textView.setText(text);
	}
}
