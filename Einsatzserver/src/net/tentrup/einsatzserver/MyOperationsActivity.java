package net.tentrup.einsatzserver;

import java.util.List;

import android.view.View;
import android.widget.TextView;

import net.tentrup.einsatzserver.comm.Communicator;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.ResultWrapper;

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
	protected void updatePersonnelTextView(Operation operation, TextView textView) {
		textView.setVisibility(View.GONE);
	}
}
