package net.tentrup.einsatzserver;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter for the items on the home screen.
 * 
 * @author Tentrup
 *
 */
public class HomeScreenAdapter extends BaseAdapter {

	private final Context m_applicationContext;
	private final LayoutInflater m_inflater;
	private final List<HomeScreenItem> m_items;

	public HomeScreenAdapter(Context applicationContext) {
		this.m_applicationContext = applicationContext;
		m_items = new ArrayList<HomeScreenItem>(3);
		m_items.add(new HomeScreenItem(R.string.menu_my, MyOperationsActivity.class));
		m_items.add(new HomeScreenItem(R.string.menu_all, AllOperationsActivity.class));
		m_items.add(new HomeScreenItem(R.string.menu_configuration, ConfigurationActivity.class));
		m_inflater = (LayoutInflater)applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return m_items.size();
	}

	public Class<? extends Activity> getActivityClass(int position) {
		return m_items.get(position).getActivityClass();
	}

	@Override
	public String getItem(int position) {
		return getStringResource(m_items.get(position).getResourceId());
	}

	private String getStringResource(int resourceId) {
		return m_applicationContext.getResources().getString(resourceId);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        String title = getItem(position);
        if (convertView == null) {
            tv = (TextView) m_inflater.inflate(R.layout.home_screen_list_item, parent, false);
            tv.setGravity(Gravity.CENTER);
        } else {
            tv = (TextView) convertView;
        }
        tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        tv.setText(title);
        return tv;
	}

}
