package net.tentrup.einsatzserver.parser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.Person;
import net.tentrup.einsatzserver.model.Resource;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
		checkOperation(first, 7714, "DIE", null, new LocalDate(2011, 9, 18), new LocalTime(12, 30), "Fortuna II gegen Schalke 04 II", "Paul-Janes-Stadion, Flinger Broich", 5, 0, 1, new LocalDateTime(2011, 8, 16, 11, 16), "L, M");
		Operation second = operations.get(1);
		checkOperation(second, 7505, "DIE", null, new LocalDate(2011, 9, 19), null, "Schützenfest Unterrath", "Festplatz Karthäuser Straße", 2, 2, 0, new LocalDateTime(2011, 9, 13, 20, 52), "M, T");
		Operation third = operations.get(2);
		checkOperation(third, 7507, "DIE", null, new LocalDate(2011, 9, 20), new LocalTime(14, 0), "Schützenfest Unterrath", "Festplatz Karthäuser Straße", 1, 1, 0, new LocalDateTime(2011, 9, 13, 20, 52), "M, T");
	}

	@Test
	public void testParseMyOperationsPage() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/myOperationsPage.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		List<Operation> operations = new HtmlParser().parseMyOperationsPage(content).getResult();
		Assert.assertEquals("wrong number of rows.", 2, operations.size());
		Operation first = operations.get(0);
		checkOperation(first, 7878, "GEM", BookingState.REQUESTED, new LocalDate(2011, 9, 16), new LocalTime(19, 30), "Gr.", "Zugheim Wersten", 0, 0, 0, null, null);
		Operation second = operations.get(1);
		checkOperation(second, 7879, "GEM", BookingState.REQUESTED, new LocalDate(2011, 9, 17), null, "Test", "Zugheim Wersten", 0, 0, 0, null, null);
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
		checkOperation(first, 8376, "AUS", BookingState.CONFIRMED, new LocalDate(2011, 11, 26), new LocalTime(17, 0), "Ü. g. V.", "Düsseldorf", 0, 0, 0, null, null);
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
		Operation inputOperation = new Operation();
		inputOperation.setId(7894);
		OperationDetails operationDetails = new HtmlParser().parseOperationDetailsPage(inputOperation, content).getResult();
		Assert.assertEquals("Tentrup, Stephan", operationDetails.getUsername());
		checkOperation(operationDetails, 7894, null, null, new LocalDate(2011, 9, 25), new LocalTime(13, 0), "B. Düsseldorf", "Düsseldorf", 15, 13, 1, null, null);
		StringBuilder commentBuilder = new StringBuilder();
		commentBuilder.append("....");
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append("abc");
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append("..:");
		commentBuilder.append(System.getProperty("line.separator"));
		List<Person> personnel = new ArrayList<Person>();
		personnel.add(getPerson("D.", "M.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("E.", "F.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("F.", "T.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("H.", "S.", "A", BookingState.REQUESTED, null, "Mitte", null, null));
		personnel.add(getPerson("K.", "S.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("K.", "S.", null, BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("L.", "S.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("N.", "M.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("N.", "S.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("S.", "F.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("S.", "T.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("T.", "S.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("W.", "B.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("W.", "K.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		checkOperationDetails(operationDetails, new LocalDate(2011, 9, 25), new LocalTime(20, 0), "DRK-Einsatzzentrum, Erkrather Str. 208", new LocalDate(2011, 9, 25), new LocalTime(13, 0), false, commentBuilder.toString(), null, null, personnel, new ArrayList<Resource>());
	}

	@Test
	public void testParseOperationDetailsPage2() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/operationDetailsPage2.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		Operation inputOperation = new Operation();
		inputOperation.setId(8516);
		OperationDetails operationDetails = new HtmlParser().parseOperationDetailsPage(inputOperation, content).getResult();
		Assert.assertEquals("Tentrup, Stephan", operationDetails.getUsername());
		checkOperation(operationDetails, 8516, null, null, new LocalDate(2011, 12, 3), new LocalTime(16, 0), "E/W:R. o. C.", "E. A.", 4, 4, 0, null, null);
		StringBuilder commentBuilder = new StringBuilder();
		commentBuilder.append("2 H. EH");
		commentBuilder.append(System.getProperty("line.separator"));
		commentBuilder.append("2 H. O.");
		List<Person> personnel = new ArrayList<Person>();
		personnel.add(getPerson("A.", "B.", "A", BookingState.ABSENT, "Test1", "Mitte", new LocalTime(16, 0), new LocalTime(23, 0)));
		personnel.add(getPerson("L.", "S.", "A", BookingState.CONFIRMED, "Test2", "Mitte", null, null));
		personnel.add(getPerson("S.", "H. J.", "A", BookingState.CONFIRMED, null, "Mitte", new LocalTime(17, 0), new LocalTime(20, 0)));
		personnel.add(getPerson("T.", "S.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		personnel.add(getPerson("W.", "B.", "A", BookingState.CONFIRMED, null, "Mitte", null, null));
		checkOperationDetails(operationDetails, new LocalDate(2011, 12, 3), new LocalTime(23, 0), "DRK-Einsatzzentrum, Erkrather Str. 208", new LocalDate(2011, 12, 3), new LocalTime(15, 15), true, commentBuilder.toString(), "Kontaktperson", "0211/123", personnel, new ArrayList<Resource>());
	}

	@Test
	public void testParseOperationDetailsPage3() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/operationDetailsPage3.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		Operation inputOperation = new Operation();
		inputOperation.setId(8152);
		OperationDetails operationDetails = new HtmlParser().parseOperationDetailsPage(inputOperation, content).getResult();
		Assert.assertEquals("Tentrup, Stephan", operationDetails.getUsername());
		checkOperation(operationDetails, 8152, null, null, new LocalDate(2012, 7, 8), null, "B. 2012 -V.-", "A. P.", 0, 1, 0, null, null);
		List<Person> personnel = new ArrayList<Person>(); 	 
		personnel.add(getPerson("T.", "T.", "A", BookingState.CONFIRMED, null, "FmD", null, null));
		checkOperationDetails(operationDetails, new LocalDate(2012, 7, 8), null, "DRK-Einsatzzentrum, Erkrather Str. 208", new LocalDate(2012, 7, 8), null, false, null, null, null, personnel, new ArrayList<Resource>());
	}

	@Test
	public void testParseOperationDetailsPage4() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/operationDetailsPage4.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		Operation inputOperation = new Operation();
		inputOperation.setId(8690);
		OperationDetails operationDetails = new HtmlParser().parseOperationDetailsPage(inputOperation, content).getResult();
		Assert.assertEquals("Tentrup, Stephan", operationDetails.getUsername());
		checkOperation(operationDetails, 8690, null, null, new LocalDate(2011, 12, 16), new LocalTime(16, 0), "F. gegen P.", "Arena, Arena-Str.", 4, 0, 0, null, null);
		List<Person> personnel = new ArrayList<Person>();
		List<Resource> resources = new ArrayList<Resource>();
		resources.add(getResource("22-85-10" + System.getProperty("line.separator") + "22-KTW-10"));
		resources.add(getResource("22-83-02" + System.getProperty("line.separator") + "22-ITW-02"));
		resources.add(getResource("08-19-01" + System.getProperty("line.separator") + "08-MTF-01"));
		checkOperationDetails(operationDetails, new LocalDate(2011, 12, 16), new LocalTime(21, 30), "DRK-Einsatzzentrum, Erkrather Str. 208", new LocalDate(2011, 12, 16), new LocalTime(15, 0), false, null, null, null, personnel, resources);
	}

	@Test
	public void testParseBookingPage() throws Exception {
		FileInputStream inputStream = new FileInputStream("resource/booking_successful.html");
		byte[] byteContent = new byte[inputStream.available()];
		inputStream.read(byteContent);
		String content = new String(byteContent);
		Operation inputOperation = new Operation();
		inputOperation.setId(8560);
		OperationDetails operationDetails = new HtmlParser().parseOperationDetailsPage(inputOperation, content).getResult();
		Assert.assertEquals("Tentrup, Stephan", operationDetails.getUsername());
		checkOperation(operationDetails, 8560, null, null, new LocalDate(2011, 12, 9), new LocalTime(20, 0), "W: Gr.", "Z. W.", 0, 0, 4, null, null);
		List<Person> personnel = new ArrayList<Person>(); 	 
		personnel.add(getPerson("M", "H", "A", BookingState.REQUESTED, null, "Mitte", null, null));
		personnel.add(getPerson("R", "R", "A", BookingState.REQUESTED, null, "Mitte", null, null));
		personnel.add(getPerson("T", "S", "A", BookingState.REQUESTED, null, "Mitte", null, null));
		personnel.add(getPerson("V", "K", "A", BookingState.REQUESTED, null, "Mitte", null, null));
		checkOperationDetails(operationDetails, new LocalDate(2011, 12, 9), new LocalTime(22, 0), "D-Z", new LocalDate(2011, 12, 9), new LocalTime(19, 30), false, null, null, null, personnel, new ArrayList<Resource>());
	}

	private Person getPerson(String surname, String name, String qualification, BookingState bookingState, String comment, String division, LocalTime startTime, LocalTime endTime) {
		Person person = new Person();
		person.setName(name);
		person.setSurname(surname);
		person.setQualification(qualification);
		person.setBookingState(bookingState);
		person.setComment(comment);
		person.setDivision(division);
		person.setStartTime(startTime);
		person.setEndTime(endTime);
		return person;
	}

	private Resource getResource(String name) {
		Resource resource = new Resource();
		resource.setName(name);
		return resource;
	}

	private void checkOperation(Operation operation, int id, String type, BookingState bookingState, LocalDate startDate, LocalTime startTime, String description, String location, int personnelRequested, int personnelBookingConfirmed, int personnelBookingRequested, LocalDateTime latestChangeDate, String latestChangeAuthor) {
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
		Assert.assertEquals("latest change date is wrong.", latestChangeDate, operation.getLatestChangeDate());
		Assert.assertEquals("latest change author is wrong.", latestChangeAuthor, operation.getLatestChangeAuthor());
	}

	private void checkOperationDetails(OperationDetails operationDetails, LocalDate endDate, LocalTime endTime, String reportLocation, LocalDate reportDate, LocalTime reportTime, boolean catering, String comment, String contactPerson, String contactPersonPhone, List<Person> personnel, List<Resource> resources) {
		Assert.assertEquals("endDate is wrong.", endDate, operationDetails.getEndDate());
		Assert.assertEquals("endTime is wrong.", endTime, operationDetails.getEndTime());
		Assert.assertEquals("reportLocation is wrong.", reportLocation, operationDetails.getReportLocation());
		Assert.assertEquals("reportDate is wrong.", reportDate, operationDetails.getReportDate());
		Assert.assertEquals("reportTime is wrong.", reportTime, operationDetails.getReportTime());
		Assert.assertEquals("catering is wrong.", catering, operationDetails.isCatering());
		Assert.assertEquals("comment is wrong.", comment, operationDetails.getComment());
		Assert.assertEquals("contactPerson is wrong.", contactPerson, operationDetails.getContactPerson());
		Assert.assertEquals("contactPersonPhone is wrong.", contactPersonPhone, operationDetails.getContactPersonPhone());
		checkPersonnel(operationDetails, personnel);
		checkResources(operationDetails, resources);
	}

	private void checkPersonnel(OperationDetails operationDetails, List<Person> personnel) {
		Assert.assertEquals("personnel count is wrong.", personnel.size(), operationDetails.getPersonnel().size());
		for (int i = 0; i < personnel.size(); i++) {
			Person expected = personnel.get(i);
			Person actual = operationDetails.getPersonnel().get(i);
			Assert.assertEquals("name is wrong.", expected.getName(), actual.getName());
			Assert.assertEquals("surname is wrong.", expected.getSurname(), actual.getSurname());
			Assert.assertEquals("bookingState is wrong.", expected.getBookingState(), actual.getBookingState());
			Assert.assertEquals("comment is wrong", expected.getComment(), actual.getComment());
			Assert.assertEquals("division is wrong", expected.getDivision(), actual.getDivision());
			Assert.assertEquals("startTime is wrong", expected.getStartTime(), actual.getStartTime());
			Assert.assertEquals("endTime is wrong", expected.getEndTime(), actual.getEndTime());
			Assert.assertEquals("qualification is wrong.", expected.getQualification(), actual.getQualification());
		}
	}

	private void checkResources(OperationDetails operationDetails, List<Resource> resources) {
		Assert.assertEquals("resource count is wrong.", resources.size(), operationDetails.getResources().size());
		for (int i = 0; i < resources.size(); i++) {
			Resource expected = resources.get(i);
			Resource actual = operationDetails.getResources().get(i);
			Assert.assertEquals("name is wrong", expected.getName(), actual.getName());
		}
	}
}
