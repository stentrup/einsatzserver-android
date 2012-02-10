package net.tentrup.einsatzserver;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

/**
 * Activity test case for {@link HomeScreenActivity}.
 *
 * @author Tentrup
 *
 */
public class HomeScreenActivityTest extends ActivityInstrumentationTestCase2<HomeScreenActivity> {

	private HomeScreenActivity m_activity;
	private ListView m_listView;

	public HomeScreenActivityTest() {
		super("net.tentrup.einsatzserver", HomeScreenActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		m_activity = getActivity();
		m_listView = (ListView) m_activity.findViewById(android.R.id.list);
	}

	public void testPreConditions() {
		assertEquals(4, m_listView.getChildCount());
	}
}
