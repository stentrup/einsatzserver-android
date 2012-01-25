package net.tentrup.einsatzserver;

import java.util.List;

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
}
