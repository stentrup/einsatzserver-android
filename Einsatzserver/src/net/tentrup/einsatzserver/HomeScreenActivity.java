package net.tentrup.einsatzserver;

import greendroid.app.GDListActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import net.tentrup.einsatzserver.comm.Communicator;
import net.tentrup.einsatzserver.config.PreferenceKeys;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Shows the home screen of the application
 * 
 * @author Tentrup
 *
 */
public class HomeScreenActivity extends GDListActivity {

	private static final int ALERT_DIALOG_CONFIGURATION_ERROR = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final HomeScreenAdapter homeScreenAdapter = new HomeScreenAdapter(getApplicationContext());
		setListAdapter(homeScreenAdapter);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(HomeScreenActivity.this, homeScreenAdapter.getActivityClass(position));
				startActivity(intent);
			}
		});
		getActionBar().setType(ActionBar.Type.Empty); //do not show home button
        addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(new ActionBarDrawable(this, R.drawable.ic_action_bar_info)), R.id.action_bar_view_info);
		setTitle(R.string.app_name);
		// check if app was started in test mode
		boolean testmode = getIntent().getBooleanExtra(Communicator.PREF_TESTMODE, false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putBoolean(Communicator.PREF_TESTMODE, testmode);
		editor.commit();

		checkLoginData();
		new Eula(this).show();
	}

	/**
	 * Checks if username and password are set. Shows message otherwise.
	 */
	private void checkLoginData() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getString(PreferenceKeys.CONFIGURATION_USERNAME, "").trim().length() < 1 
				|| prefs.getString(PreferenceKeys.CONFIGURATION_PASSWORD, "").trim().length() < 1) {
			showDialog(ALERT_DIALOG_CONFIGURATION_ERROR);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == ALERT_DIALOG_CONFIGURATION_ERROR) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.alert_configuration_error)
			       .setCancelable(false)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(HomeScreenActivity.this, ConfigurationActivity.class);
							startActivity(intent);
			           }
			       });
			AlertDialog alert = builder.create();
			return alert;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public int createLayout() {
		return R.layout.home_screen_list;
	}

    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_bar_view_info:
                startActivity(new Intent(this, InfoActivity.class));
                return true;
            default:
                return super.onHandleActionBarItemClick(item, position);
        }
    }
}
