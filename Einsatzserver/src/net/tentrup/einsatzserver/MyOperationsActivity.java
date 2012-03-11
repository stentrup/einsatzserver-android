package net.tentrup.einsatzserver;

import java.util.List;

import net.tentrup.einsatzserver.comm.Communicator;
import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.ResultWrapper;
import android.widget.TextView;

/**
 * Activity showing my operations.
 * 
 * @author Tentrup
 *
 */
public class MyOperationsActivity extends AbstractOperationsActivity {

	@Override
	protected ResultWrapper<List<Operation>> getActivityResult() {
		return new Communicator(getApplicationContext()).getMyOperations();
	}

	@Override
	protected void updateStateTextView(Operation operation, TextView textView) {
		BookingState bookingState = operation.getBookingState();
		textView.setText(getString(bookingState.getResourceIdShort()));
	}

	@Override
	protected void addToActionBar() {
		// no action bar item
	}

	@Override
	protected boolean showItem(Operation operation) {
		// no filtering
		return true;
	}
}
