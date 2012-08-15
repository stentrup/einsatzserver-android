package net.tentrup.einsatzserver;

import static net.tentrup.einsatzserver.TestUtil.createOperation;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.tentrup.einsatzserver.comm.Communicator;
import net.tentrup.einsatzserver.comm.CommunicatorSingleton;
import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.ResultStateEnum;
import net.tentrup.einsatzserver.model.ResultWrapper;

import org.mockito.Mockito;

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
	private Communicator mockedCommunicator;

	public HomeScreenActivityTest() {
		super(HomeScreenActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		mockedCommunicator = Mockito.mock(Communicator.class);
		when(mockedCommunicator.login(Mockito.anyString(), Mockito.anyString())).thenReturn(ResultStateEnum.SUCCESSFUL);
		List<Operation> result = new ArrayList<Operation>();
		Operation operation1 = createOperation(1, "Testeintrag 1", "2012-07-25", BookingState.CONFIRMED);
		result.add(operation1);
		Operation operation2 = createOperation(2, "Testeintrag 2", "2012-07-30", BookingState.CONFIRMED);
		result.add(operation2);
		when(mockedCommunicator.getMyOperations()).thenReturn(new ResultWrapper<List<Operation>>(result, ResultStateEnum.SUCCESSFUL));
		when(mockedCommunicator.getAllOperations()).thenReturn(new ResultWrapper<List<Operation>>(result, ResultStateEnum.SUCCESSFUL));
		CommunicatorSingleton.setCommunictor(mockedCommunicator);
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
