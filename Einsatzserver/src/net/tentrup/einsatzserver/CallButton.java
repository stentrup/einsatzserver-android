package net.tentrup.einsatzserver;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

public class CallButton implements ImageButtonDefinition {

	private final String m_phoneNumber;
	private final Activity m_parent;

	public CallButton(Activity parent, String phoneNumber) {
		m_phoneNumber = phoneNumber;
		m_parent = parent;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("tel:" + m_phoneNumber));
		try {
			m_parent.startActivity(intent);
		} catch (ActivityNotFoundException exc) {
			Toast.makeText(m_parent, R.string.alert_intent_error, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public int getImageResourceId() {
		return R.drawable.call;
	}
}
