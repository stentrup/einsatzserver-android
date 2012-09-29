package net.tentrup.einsatzserver;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

public class MapsButton implements ImageButtonDefinition {

	private final String m_text;
	private final Activity m_parent;

	public MapsButton(Activity parent, String text) {
		m_parent = parent;
		m_text = text;
	}

	@Override
	public void onClick(View v) {
		showOnMap(m_text);
	}

	private void showOnMap(String location) {
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + location));
		try {
			m_parent.startActivity(intent);
		} catch (ActivityNotFoundException exc) {
			Toast.makeText(m_parent, R.string.alert_map_error, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public int getImageResourceId() {
		return R.drawable.map_marker;
	}
}
