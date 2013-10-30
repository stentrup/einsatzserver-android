package net.tentrup.einsatzserver.parser.contenthandler.details;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.Person;
import net.tentrup.einsatzserver.parser.ParseUtil;

import org.xml.sax.ContentHandler;

/**
 * {@link ContentHandler} for the HTML table which contains the operation personnel.
 *
 * @author Tentrup
 *
 */
public class PersonnelTableContentHandler extends OperationDetailsTableContentHandler<Person> {

	public PersonnelTableContentHandler(OperationDetails operationDetails) {
		super(operationDetails);
	}

	private static final Map<String, TableCellContentHandler<Person>> LABEL_TO_HANDLER_MAP = new HashMap<String, TableCellContentHandler<Person>>();
	static {
		LABEL_TO_HANDLER_MAP.put("St.", new TableCellContentHandler<Person>() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setBookingState(BookingState.parseShortText(content));
			}
		});
		LABEL_TO_HANDLER_MAP.put("Ber.", new TableCellContentHandler<Person>() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setDivision(content);
			}
		});
		LABEL_TO_HANDLER_MAP.put("Name", new TableCellContentHandler<Person>() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setSurname(content);
			}
		});
		LABEL_TO_HANDLER_MAP.put("Vorname", new TableCellContentHandler<Person>() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setName(content);
			}
		});
		LABEL_TO_HANDLER_MAP.put("Ausb.", new TableCellContentHandler<Person>() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setQualification(content);
			}
		});
		LABEL_TO_HANDLER_MAP.put("Dienst-\nbeginn", new TableCellContentHandler<Person>() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setStartTime(ParseUtil.parseTime(content));
			}
		});
		LABEL_TO_HANDLER_MAP.put("Dienst-\nende", new TableCellContentHandler<Person>() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setEndTime(ParseUtil.parseTime(content));
			}
		});
		LABEL_TO_HANDLER_MAP.put("Bemerkung", new TableCellContentHandler<Person>() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setComment(content);
			}
		});
	}

	@Override
	public Map<String, TableCellContentHandler<Person>> getLabelToHandlerMap() {
		return LABEL_TO_HANDLER_MAP;
	}

	@Override
	public Person createNewItem() {
		return new Person();
	}

	@Override
	public List<Person> getItemList() {
		return m_operationDetails.getPersonnel();
	}
}
