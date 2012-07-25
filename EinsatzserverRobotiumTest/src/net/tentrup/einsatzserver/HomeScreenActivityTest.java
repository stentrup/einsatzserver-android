package net.tentrup.einsatzserver;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

/**
 * Robotium-Test für die {@link HomeScreenActivity}.
 *
 * @author Tentrup
 *
 */
public class HomeScreenActivityTest extends ActivityInstrumentationTestCase2<HomeScreenActivity> {

	private Solo solo;

	public HomeScreenActivityTest() {
		super(HomeScreenActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	public void testDashboard() {
		Assert.assertTrue(solo.searchButton(getActivity().getString(R.string.menu_my)));
		Assert.assertTrue(solo.searchButton(getActivity().getString(R.string.menu_all)));
		Assert.assertTrue(solo.searchButton(getActivity().getString(R.string.menu_configuration)));
		Assert.assertTrue(solo.searchButton(getActivity().getString(R.string.menu_help)));
	}

	public void testMyOperationsActivity() {
		solo.clickOnButton(getActivity().getString(R.string.menu_my));
		solo.assertCurrentActivity("Expected My Operations activity", MyOperationsActivity.class);
		solo.waitForText(getActivity().getString(R.string.my_title));
	}

	public void testAllOperationsActivity() {
		solo.clickOnButton(getActivity().getString(R.string.menu_all));
		solo.assertCurrentActivity("Expected All Operations activity", AllOperationsActivity.class);
		solo.waitForText(getActivity().getString(R.string.all_title));
	}

	public void testConfigurationActivity() {
		solo.clickOnButton(getActivity().getString(R.string.menu_configuration));
		solo.assertCurrentActivity("Expected Configuration activity", ConfigurationActivity.class);
	}

	public void testHelpActivity() {
		solo.clickOnButton(getActivity().getString(R.string.menu_help));
		solo.assertCurrentActivity("Expected Help activity", HelpActivity.class);
	}

	public void testAboutActivity() {
		solo.clickOnImageButton(0);
		solo.assertCurrentActivity("Expected Info activity", InfoActivity.class);
	}
}
