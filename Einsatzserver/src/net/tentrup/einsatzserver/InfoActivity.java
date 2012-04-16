package net.tentrup.einsatzserver;

import greendroid.app.GDActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

/**
 * Dialog which shows some information about the application.
 * 
 * @author Tentrup
 *
 */
public class InfoActivity extends GDActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(createTitle());
		setActionBarContentView(R.layout.info_tabs);
		PagerAdapter mAdapter = new InfoPagerAdapter();
		ViewPager mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		PageIndicator mIndicator = (TabPageIndicator)findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
	}

	private String createTitle() {
		StringBuilder infoTextBuilder = new StringBuilder();
		infoTextBuilder.append(getString(R.string.app_name));
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = pinfo.versionName;
			infoTextBuilder.append(" v");
			infoTextBuilder.append(versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return infoTextBuilder.toString();
	}

	private class InfoPagerAdapter extends PagerAdapter implements TitleProvider {

		private List<String> itemLabels = new ArrayList<String>();
		private List<View> itemViews = new ArrayList<View>();

		public InfoPagerAdapter() {
			addTab(getString(R.string.about_title), Html.fromHtml(readAboutText(InfoActivity.this)));
			addTab(getString(R.string.changes_title), RecentChanges.readRecentChanges(InfoActivity.this));
			addTab(getString(R.string.license_title), Eula.readEula(InfoActivity.this));
		}

		private void addTab(String title, CharSequence content) {
			itemLabels.add(title);
			View view = getLayoutInflater().inflate(R.layout.info_tab, null);
			TextView tabContent = (TextView) view.findViewById(R.id.info_tab_content);
			tabContent.setText(content);
			tabContent.setMovementMethod(LinkMovementMethod.getInstance());
			itemViews.add(view);
		}

		@Override
		public String getTitle(int position) {
			return itemLabels.get(position).toUpperCase();
		}

		@Override
		public int getCount() {
			return itemLabels.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View item = itemViews.get(position);
			container.addView(item, 0);
			return item;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}

	private static String readAboutText(Activity activity) {
		BufferedReader reader = null;
		try {
			InputStream stream = activity.getResources().openRawResource(R.raw.about);
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
