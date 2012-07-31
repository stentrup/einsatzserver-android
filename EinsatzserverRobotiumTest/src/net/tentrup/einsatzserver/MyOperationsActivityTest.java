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
import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.Person;
import net.tentrup.einsatzserver.model.ResultStateEnum;
import net.tentrup.einsatzserver.model.ResultWrapper;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.mockito.Mockito;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageView;
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

	private Operation createOperation(int id, String description, String date, BookingState bookingState) {
		Operation operation = new Operation();
		operation.setId(id);
		operation.setDescription(description);
		operation.setDate(LocalDate.parse(date));
		operation.setBookingState(bookingState);
		return operation;
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
		OperationDetails operationDetails = new OperationDetails();
		operationDetails.setCatering(false);
		operationDetails.setComment("Dies ist ein Testeintrag");
		operationDetails.setDate(operation1.getStartDate());
		operationDetails.setDescription(operation1.getDescription());
		operationDetails.setEndDate(operation1.getStartDate());
		operationDetails.setEndTime(LocalTime.parse("20:00:00"));
		operationDetails.setId(operation1.getId());
		operationDetails.setLatestChangeAuthor("Mr. X");
		operationDetails.setLatestChangeDate(LocalDateTime.parse("2012-06-30T21:30:23"));
		operationDetails.setLocation("Düsseldorf");
		operationDetails.setPersonnel(new ArrayList<Person>());
		operationDetails.setPersonnelBookingConfirmed(0);
		operationDetails.setPersonnelBookingRequested(0);
		operationDetails.setPersonnelRequested(4);
		operationDetails.setReportDate(operation1.getStartDate());
		operationDetails.setReportTime(LocalTime.parse("12:30:00"));
		operationDetails.setReportLocation("Treffpunkt");
		operationDetails.setStartTime(LocalTime.parse("13:00:00"));
		when(mockedCommunicator.getOperationDetails(Mockito.any(Operation.class))).thenReturn(new ResultWrapper<OperationDetails>(operationDetails, ResultStateEnum.SUCCESSFUL));
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
		// Spaltenkonfiguration ggf. zurücksetzen
		solo.clickOnImageButton(1);
		solo.waitForText(getActivity().getString(R.string.visible_columns));
		if (!solo.isTextChecked(getActivity().getString(R.string.configuration_ui_showDayOfWeek))) {
			solo.clickOnText(getActivity().getString(R.string.configuration_ui_showDayOfWeek));
		}
		solo.clickOnButton(0);
		List<TableRow> tableRows = getTableRows();
		assertEquals(2, tableRows.size());
		checkOperationTableRow(tableRows, 0, LocalDate.parse("2012-07-25"), "Testeintrag 1", true);
		checkOperationTableRow(tableRows, 1, LocalDate.parse("2012-07-30"), "Testeintrag 2", true);
		// Spaltenkonfiguration auswählen
		solo.clickOnImageButton(1);
		solo.waitForText(getActivity().getString(R.string.visible_columns));
		solo.clickOnText(getActivity().getString(R.string.configuration_ui_showDayOfWeek));
		solo.clickOnButton(0);
		tableRows = getTableRows();
		assertEquals(2, tableRows.size());
		checkOperationTableRow(tableRows, 0, LocalDate.parse("2012-07-25"), "Testeintrag 1", false);
		checkOperationTableRow(tableRows, 1, LocalDate.parse("2012-07-30"), "Testeintrag 2", false);
		solo.clickOnText("Testeintrag 1");
		assertTrue(solo.waitForText(getActivity().getString(R.string.details_title)));
		verify(mockedCommunicator).getOperationDetails(Mockito.any(Operation.class));
		verifyNoMoreInteractions(mockedCommunicator);
		//TODO: Details testen
	}

	private List<TableRow> getTableRows() {
		TableLayout table = (TableLayout)solo.getView(R.id.operations_table);
		List<TableRow> tableRows = new ArrayList<TableRow>();
		for (int i = 0; i < table.getChildCount(); i++) {
			View child = table.getChildAt(i);
			if (child instanceof TableRow) {
				TableRow tableRow = (TableRow)child;
				tableRows.add(tableRow);
			}
		}
		return tableRows;
	}

	private void checkOperationTableRow(List<TableRow> tableRows, int index, LocalDate date, String description, boolean dayOfWeekVisible) {
		TableRow tableRow = tableRows.get(index);
		TextView descriptionTextView = (TextView)tableRow.findViewById(R.id.cell_description);
		assertEquals(description, descriptionTextView.getText());
		TextView dayOfWeekView = (TextView)tableRow.findViewById(R.id.cell_day_of_week);
		assertEquals(date.toString("EE"), dayOfWeekView.getText());
		if (dayOfWeekVisible) {
			assertEquals(View.VISIBLE, dayOfWeekView.getVisibility());
		} else {
			assertEquals(View.GONE, dayOfWeekView.getVisibility());
		}
		TextView dateTextView = (TextView)tableRow.findViewById(R.id.cell_date);
		assertEquals(date.toString("dd.MM.yy"), dateTextView.getText());
		ImageView stateImageView = (ImageView)tableRow.findViewById(R.id.cell_state_image);
		assertNotNull(stateImageView.getDrawable());
		assertEquals(View.VISIBLE, stateImageView.getVisibility());
		TextView stateTextView = (TextView) tableRow.findViewById(R.id.cell_state_text);
		assertEquals(View.GONE, stateTextView.getVisibility());
	}
}
