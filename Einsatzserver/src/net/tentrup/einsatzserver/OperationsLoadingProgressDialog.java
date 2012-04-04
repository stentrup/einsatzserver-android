package net.tentrup.einsatzserver;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Dialog which is shown while operations are loaded.
 * 
 * @author Tentrup
 *
 */
public class OperationsLoadingProgressDialog extends AsyncTaskProgressDialog {

	private static final String TAG = OperationsLoadingProgressDialog.class.getSimpleName();

	private final Activity m_parent;

	public OperationsLoadingProgressDialog(Activity parent, AsyncTask<Void, Void, Void> task) {
		super(parent, task);
		m_parent = parent;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.i(TAG, "Back button has been pressed. Finish parent activity");
		m_parent.finish();
	}

}
