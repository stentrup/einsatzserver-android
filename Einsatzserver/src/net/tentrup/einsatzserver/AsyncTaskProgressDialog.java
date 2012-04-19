package net.tentrup.einsatzserver;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskProgressDialog extends ProgressDialog {

	private static final String TAG = AsyncTaskProgressDialog.class.getSimpleName();

	private final AsyncTask<Void, Void, Void> m_task;

	public AsyncTaskProgressDialog(Context context, AsyncTask<Void, Void, Void> task, String message) {
		super(context, R.style.PopupStyle);
		m_task = task;
		setMessage(message);
		setIndeterminate(true);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.i(TAG, "Back button has been pressed. Cancel async task");
		m_task.cancel(false);
	}

}
