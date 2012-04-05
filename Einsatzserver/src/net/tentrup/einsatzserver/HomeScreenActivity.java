package net.tentrup.einsatzserver;

import greendroid.app.GDActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Shows the home screen of the application
 * 
 * @author Tentrup
 *
 */
public class HomeScreenActivity extends GDActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.home_screen);
		findViewById(R.id.my_operations).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeScreenActivity.this, MyOperationsActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.all_operations).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeScreenActivity.this, AllOperationsActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.configuration).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeScreenActivity.this, ConfigurationActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeScreenActivity.this, HelpActivity.class);
				startActivity(intent);
			}
		});
		getActionBar().setType(ActionBar.Type.Empty); //do not show home button
        addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(new ActionBarDrawable(this, R.drawable.ic_action_bar_info)), R.id.action_bar_view_info);
		setTitle(R.string.app_name);
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
