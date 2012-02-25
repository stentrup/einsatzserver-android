package net.tentrup.einsatzserver;

import greendroid.app.GDActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

/**
 * Activity that shows the user manual
 *
 * @author Tentrup
 *
 */
public class HelpActivity extends GDActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.help);
		TextView tv = (TextView) findViewById(R.id.help);
		tv.setText(Html.fromHtml(readHelp()));
	}

	private String readHelp() {
		BufferedReader reader = null;
		try {
			InputStream stream = getResources().openRawResource(R.raw.help);
			reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				buffer.append(line).append(System.getProperty("line.separator"));
			}
			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException exc) {
					// Ignore
				}
			}
		}
	}
}
