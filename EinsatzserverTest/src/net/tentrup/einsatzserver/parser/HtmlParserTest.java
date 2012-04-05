package net.tentrup.einsatzserver.parser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.Person;
import net.tentrup.einsatzserver.model.ResultWrapper;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link HtmlParser}.
 * 
 * @author Tentrup
 *
 */
public class HtmlParserTest {

	@Test
	public void testParseAllOperationsPage() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/allOperationsPage.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		List<Operation> operations = new HtmlParser().parseAllOperationsPage(content).getResult();
		Assert.assertEquals("wrong number of rows.", 3, operations.size());
		Operation first = operations.get(0);
		checkOperation(first, 7714, "DIE", null, new LocalDate(2011, 9, 18), new LocalTime(12, 30), "Fortuna II gegen Schalke 04 II", "Paul-Janes-Stadion, Flinger Broich", 5, 0, 1);
		Operation last = operations.get(operations.size() - 1);
		checkOperation(last, 7507, "DIE", null, new LocalDate(2011, 9, 20), new LocalTime(14, 0), "Schützenfest Unterrath", "Festplatz Karthäuser Straße", 1, 1, 0);
	}

	@Test
	public void testParseMyOperationsPage() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/myOperationsPage.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		List<Operation> operations = new HtmlParser().parseMyOperationsPage(content).getResult();
		Assert.assertEquals("wrong number of rows.", 1, operations.size());
		Operation first = operations.get(0);
		checkOperation(first, 7878, "GEM", BookingState.REQUESTED, new LocalDate(2011, 9, 16), new LocalTime(19, 30), "Gr.", "Zugheim Wersten", 0, 0, 0);
	}

	@Test
	public void testParseMyOperationsPageBooked() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/myOperationsPageBooked.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		List<Operation> operations = new HtmlParser().parseMyOperationsPage(content).getResult();
		Assert.assertEquals("wrong number of rows.", 1, operations.size());
		Operation first = operations.get(0);
		checkOperation(first, 8376, "AUS", BookingState.CONFIRMED, new LocalDate(2011, 11, 26), new LocalTime(17, 0), "Ü. g. V.", "Düsseldorf", 0, 0, 0);
	}

	@Test
	public void testParseEmptyMyOperationsPage() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/myOperationsPageEmpty.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		List<Operation> operations = new HtmlParser().parseMyOperationsPage(content).getResult();
		Assert.assertEquals("wrong number of rows.", 0, operations.size());
	}

	@Test
	public void testParseOperationDetailsPage() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/operationDetailsPage.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		ResultWrapper<OperationDetails> resultWrapper = new HtmlParser().parseOperationDetailsPage(7894, content);
		Assert.assertEquals("Tentrup, Stephan", resultWrapper.getUsername());
		OperationDetails operationDetails = resultWrapper.getResult();
		checkOperation(operationDetails, 7894, null, null, new LocalDate(2011, 9, 25), new LocalTime(13, 0), "B. Düsseldorf", "Düsseldorf", 15, 13, 1);
		StringBuilder commentBuilder = new StringBuilder();
		commentBuilder.append("....");
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append("abc");
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append("..:");
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append(System.getProperty("line.separator"));
		List<Person> personnel = new ArrayList<Person>();
		personnel.add(getPerson("D.", "M.", BookingState.CONFIRMED));
		personnel.add(getPerson("E.", "F.", BookingState.CONFIRMED));
		personnel.add(getPerson("F.", "T.", BookingState.CONFIRMED));
		personnel.add(getPerson("H.", "S.", BookingState.REQUESTED));
		personnel.add(getPerson("K.", "S.", BookingState.CONFIRMED));
		personnel.add(getPerson("K.", "S.", BookingState.CONFIRMED));
		personnel.add(getPerson("L.", "S.", BookingState.CONFIRMED));
		personnel.add(getPerson("N.", "M.", BookingState.CONFIRMED));
		personnel.add(getPerson("N.", "S.", BookingState.CONFIRMED));
		personnel.add(getPerson("S.", "F.", BookingState.CONFIRMED));
		personnel.add(getPerson("S.", "T.", BookingState.CONFIRMED));
		personnel.add(getPerson("T.", "S.", BookingState.CONFIRMED));
		personnel.add(getPerson("W.", "B.", BookingState.CONFIRMED));
		personnel.add(getPerson("W.", "K.", BookingState.CONFIRMED));
		checkOperationDetails(operationDetails, new LocalDate(2011, 9, 25), new LocalTime(20, 0), "DRK-Einsatzzentrum, Erkrather Str. 208", new LocalDate(2011, 9, 25), new LocalTime(13, 0), false, commentBuilder.toString(), personnel);
	}

	@Test
	public void testParseOperationDetailsPage2() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/operationDetailsPage2.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		ResultWrapper<OperationDetails> resultWrapper = new HtmlParser().parseOperationDetailsPage(8516, content);
		Assert.assertEquals("Tentrup, Stephan", resultWrapper.getUsername());
		OperationDetails operationDetails = resultWrapper.getResult();
		checkOperation(operationDetails, 8516, null, null, new LocalDate(2011, 12, 3), new LocalTime(16, 0), "E/W:R. o. C.", "E. A.", 4, 4, 0);
		StringBuilder commentBuilder = new StringBuilder();
		commentBuilder.append("2 H. EH");
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append("2 H. O.");
		List<Person> personnel = new ArrayList<Person>();
		personnel.add(getPerson("A.", "B.", BookingState.ABSENT));
		personnel.add(getPerson("L.", "S.", BookingState.CONFIRMED));
		personnel.add(getPerson("S.", "H. J.", BookingState.CONFIRMED));
		personnel.add(getPerson("T.", "S.", BookingState.CONFIRMED));
		personnel.add(getPerson("W.", "B.", BookingState.CONFIRMED));
		checkOperationDetails(operationDetails, new LocalDate(2011, 12, 3), new LocalTime(23, 0), "DRK-Einsatzzentrum, Erkrather Str. 208", new LocalDate(2011, 12, 3), new LocalTime(15, 15), true, commentBuilder.toString(), personnel);
	}

	@Test
	public void testParseOperationDetailsPage3() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/operationDetailsPage3.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		ResultWrapper<OperationDetails> resultWrapper = new HtmlParser().parseOperationDetailsPage(8152, content);
		Assert.assertEquals("Tentrup, Stephan", resultWrapper.getUsername());
		OperationDetails operationDetails = resultWrapper.getResult();
		checkOperation(operationDetails, 8152, null, null, new LocalDate(2012, 7, 8), null, "B. 2012 -V.-", "A. P.", 0, 1, 0);
		List<Person> personnel = new ArrayList<Person>(); 	 
		personnel.add(getPerson("T.", "T.", BookingState.CONFIRMED));
		checkOperationDetails(operationDetails, new LocalDate(2012, 7, 8), null, "DRK-Einsatzzentrum, Erkrather Str. 208", new LocalDate(2012, 7, 8), null, false, null, personnel);
	}

	@Test
	public void testParseOperationDetailsPage4() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/operationDetailsPage4.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		ResultWrapper<OperationDetails> resultWrapper = new HtmlParser().parseOperationDetailsPage(8690, content);
		Assert.assertEquals("Tentrup, Stephan", resultWrapper.getUsername());
		OperationDetails operationDetails = resultWrapper.getResult();
		checkOperation(operationDetails, 8690, null, null, new LocalDate(2011, 12, 16), new LocalTime(16, 0), "F. gegen P.", "Arena, Arena-Str.", 4, 0, 0);
		List<Person> personnel = new ArrayList<Person>(); 	 
		checkOperationDetails(operationDetails, new LocalDate(2011, 12, 16), new LocalTime(21, 30), "DRK-Einsatzzentrum, Erkrather Str. 208", new LocalDate(2011, 12, 16), new LocalTime(15, 0), false, null, personnel);
	}

	@Test
	public void testParseBookingPage() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/booking_successful.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		ResultWrapper<OperationDetails> resultWrapper = new HtmlParser().parseOperationDetailsPage(8560, content);
		Assert.assertEquals("Tentrup, Stephan", resultWrapper.getUsername());
		OperationDetails operationDetails = resultWrapper.getResult();
		checkOperation(operationDetails, 8560, null, null, new LocalDate(2011, 12, 9), new LocalTime(20, 0), "W: Gr.", "Z. W.", 0, 0, 4);
		List<Person> personnel = new ArrayList<Person>(); 	 
		personnel.add(getPerson("M", "H", BookingState.REQUESTED));
		personnel.add(getPerson("R", "R", BookingState.REQUESTED));
		personnel.add(getPerson("T", "S", BookingState.REQUESTED));
		personnel.add(getPerson("V", "K", BookingState.REQUESTED));
		checkOperationDetails(operationDetails, new LocalDate(2011, 12, 9), new LocalTime(22, 0), "D-Z", new LocalDate(2011, 12, 9), new LocalTime(19, 30), false, null, personnel);
	}

	private Person getPerson(String name, String surname, BookingState bookingState) {
		Person person = new Person();
		person.setName(name);
		person.setSurname(surname);
		person.setBookingState(bookingState);
		return person;
	}

	private void checkOperation(Operation operation, int id, String type, BookingState bookingState, LocalDate startDate, LocalTime startTime, String description, String location, int personnelRequested, int personnelBookingConfirmed, int personnelBookingRequested) {
		Assert.assertEquals("id is wrong.", id, operation.getId());
		Assert.assertEquals("type is wrong.", type, operation.getType());
		Assert.assertEquals("bookingState is wrong.", bookingState, operation.getBookingState());
		Assert.assertEquals("startDate is wrong.", startDate, operation.getStartDate());
		Assert.assertEquals("startTime is wrong.", startTime, operation.getStartTime());
		Assert.assertEquals("description is wrong.", description, operation.getDescription());
		Assert.assertEquals("location is wrong.", location, operation.getLocation());
		Assert.assertEquals("personnel requested is wrong.", personnelRequested, operation.getPersonnelRequested());
		Assert.assertEquals("personnel booking confirmed is wrong.", personnelBookingConfirmed, operation.getPersonnelBookingConfirmed());
		Assert.assertEquals("personnel booking requested is wrong.", personnelBookingRequested, operation.getPersonnelBookingRequested());
	}

	private void checkOperationDetails(OperationDetails operationDetails, LocalDate endDate, LocalTime endTime, String reportLocation, LocalDate reportDate, LocalTime reportTime, boolean catering, String comment, List<Person> personnel) {
		Assert.assertEquals("endDate is wrong.", endDate, operationDetails.getEndDate());
		Assert.assertEquals("endTime is wrong.", endTime, operationDetails.getEndTime());
		Assert.assertEquals("reportLocation is wrong.", reportLocation, operationDetails.getReportLocation());
		Assert.assertEquals("reportDate is wrong.", reportDate, operationDetails.getReportDate());
		Assert.assertEquals("reportTime is wrong.", reportTime, operationDetails.getReportTime());
		Assert.assertEquals("catering is wrong.", catering, operationDetails.isCatering());
		Assert.assertEquals("comment is wrong.", comment, operationDetails.getComment());
		checkPersonnel(operationDetails, personnel);
	}

	private void checkPersonnel(OperationDetails operationDetails, List<Person> personnel) {
		Assert.assertEquals("personnel count ist wrong.", personnel.size(), operationDetails.getPersonnel().size());
		for (int i = 0; i < personnel.size(); i++) {
			Person expected = personnel.get(i);
			Person actual = operationDetails.getPersonnel().get(i);
			Assert.assertEquals("name is wrong.", expected.getName(), actual.getName());
			Assert.assertEquals("surname is wrong.", expected.getSurname(), actual.getSurname());
			Assert.assertEquals("bookingState is wrong.", expected.getBookingState(), actual.getBookingState());
		}
	}
}
