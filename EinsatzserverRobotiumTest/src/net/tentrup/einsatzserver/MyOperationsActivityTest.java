package net.tentrup.einsatzserver;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.tentrup.einsatzserver.comm.Communicator;
import net.tentrup.einsatzserver.comm.CommunicatorSingleton;
import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.ResultStateEnum;
import net.tentrup.einsatzserver.model.ResultWrapper;

import org.joda.time.LocalDate;
import org.mockito.Mockito;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

/**
 * Robotium-Test für die {@link MyOperationsActivity}.
 *
 * @author Tentrup
 *
 */
public class MyOperationsActivityTest extends ActivityInstrumentationTestCase2<MyOperationsActivity> {

	private Solo solo;
	private Communicator mockedCommunicator;

	public MyOperationsActivityTest() {
		super(MyOperationsActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		mockedCommunicator = Mockito.mock(Communicator.class);
		when(mockedCommunicator.login(Mockito.anyString(), Mockito.anyString())).thenReturn(ResultStateEnum.SUCCESSFUL);
		List<Operation> result = new ArrayList<Operation>();
		Operation operation1 = new Operation();
		operation1.setId(1);
		operation1.setDescription("Testeintrag 1");
		operation1.setDate(LocalDate.parse("2012-07-25"));
		operation1.setBookingState(BookingState.CONFIRMED);
		result.add(operation1);
		Operation operation2 = new Operation();
		operation2.setId(1);
		operation2.setDescription("Testeintrag 2");
		operation2.setDate(LocalDate.parse("2012-07-30"));
		operation2.setBookingState(BookingState.CONFIRMED);
		result.add(operation2);
		when(mockedCommunicator.getMyOperations()).thenReturn(new ResultWrapper<List<Operation>>(result, ResultStateEnum.SUCCESSFUL));
		CommunicatorSingleton.setCommunictor(mockedCommunicator);
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	public void testOperations() {
		assertTrue(solo.waitForText(getActivity().getString(R.string.my_title)));
		verify(mockedCommunicator).getMyOperations();
		verifyNoMoreInteractions(mockedCommunicator);
		TableLayout table = (TableLayout)solo.getView(R.id.operations_table);
		assertEquals(4, table.getChildCount());
		List<TableRow> tableRows = new ArrayList<TableRow>();
		for (int i = 0; i < table.getChildCount(); i++) {
			View child = table.getChildAt(i);
			if (child instanceof TableRow) {
				TableRow tableRow = (TableRow)child;
				tableRows.add(tableRow);
			}
		}
		assertEquals(2, tableRows.size());
		TableRow tableRow1 = tableRows.get(0);
		TextView descriptionTextView1 = (TextView)tableRow1.findViewById(R.id.cell_description);
		assertEquals("Testeintrag 1", descriptionTextView1.getText());
		TableRow tableRow2 = tableRows.get(1);
		TextView descriptionTextView2 = (TextView)tableRow2.findViewById(R.id.cell_description);
		assertEquals("Testeintrag 2", descriptionTextView2.getText());
	}
}
