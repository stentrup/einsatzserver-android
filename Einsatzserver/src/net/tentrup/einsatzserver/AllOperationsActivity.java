package net.tentrup.einsatzserver;

import java.util.List;

import android.widget.TextView;

import net.tentrup.einsatzserver.comm.Communicator;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.ResultWrapper;

/**
 * Activity showing all operations.
 * 
 * @author Tentrup
 *
 */
public class AllOperationsActivity extends AbstractOperationsActivity {

	@Override
	protected ResultWrapper<List<Operation>> getActivityResult() {
		return new Communicator(getApplicationContext()).getAllOperations();
	}

	@Override
	protected void updateStateTextView(Operation operation, TextView textView) {
		String personnelText = operation.getPersonnelBookingConfirmed() + "/" + operation.getPersonnelRequested();
		if (operation.getPersonnelBookingConfirmed() < operation.getPersonnelRequested()) {
			textView.setBackgroundResource(R.color.color_operation_red);
		} else {
			textView.setBackgroundResource(R.color.color_operation_green);
		}
		textView.setText(personnelText);
	}
}
