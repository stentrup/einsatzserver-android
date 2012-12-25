package net.tentrup.einsatzserver.parser.contenthandler.details;

import java.util.HashMap;
import java.util.Map;

import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.Person;
import net.tentrup.einsatzserver.parser.ParseUtil;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * {@link ContentHandler} for the HTML table which contains the operation personnel.
 *
 * @author Tentrup
 *
 */
public class PersonnelTableContentHandler extends OperationDetailsBaseContentHandler {

	private boolean m_inTable = false;
	private int m_tableRowCount = 0;
	private int m_tableCellCount = 0;

	private StringBuilder m_cellContentBuilder = new StringBuilder();
	private Person m_currentPerson;

	private static final Map<String, PersonnelTableCellContentHandler> LABEL_TO_HANDLER_MAP = new HashMap<String, PersonnelTableCellContentHandler>();
	static {
		LABEL_TO_HANDLER_MAP.put("St.", new PersonnelTableCellContentHandler() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setBookingState(BookingState.parseShortText(content));
			}
		});
		LABEL_TO_HANDLER_MAP.put("Ber.", new PersonnelTableCellContentHandler() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setDivision(content);
			}
		});
		LABEL_TO_HANDLER_MAP.put("Name", new PersonnelTableCellContentHandler() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setSurname(content);
			}
		});
		LABEL_TO_HANDLER_MAP.put("Vorname", new PersonnelTableCellContentHandler() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setName(content);
			}
		});
		LABEL_TO_HANDLER_MAP.put("Ausb.", new PersonnelTableCellContentHandler() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setQualification(content);
			}
		});
		LABEL_TO_HANDLER_MAP.put("Dienst-beginn", new PersonnelTableCellContentHandler() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setStartTime(ParseUtil.parseTime(content));
			}
		});
		LABEL_TO_HANDLER_MAP.put("Dienst-ende", new PersonnelTableCellContentHandler() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setEndTime(ParseUtil.parseTime(content));
			}
		});
		LABEL_TO_HANDLER_MAP.put("Bemerkung", new PersonnelTableCellContentHandler() {
			@Override
			public void setCellContent(Person person, String content) {
				person.setComment(content);
			}
		});
	}

	private final Map<Integer, PersonnelTableCellContentHandler> m_indexToHandlerMap = new HashMap<Integer, PersonnelTableCellContentHandler>();

	public PersonnelTableContentHandler(OperationDetails operationDetails) {
		super(operationDetails);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if ("table".equals(localName)) {
			m_inTable = true;
		}
		if (m_inTable) {
			if ("tr".equals(localName)) {
				m_tableRowCount++;
				m_tableCellCount = 0;
				m_currentPerson = new Person();
			} else if ("td".equals(localName)) {
				m_tableCellCount++;
				m_cellContentBuilder = new StringBuilder();
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (m_inTable) {
			String cellText = ParseUtil.parseString(m_cellContentBuilder.toString());
			if (m_tableRowCount == 1) {
				// Table Header
				if ("td".equals(localName)) {
					if (LABEL_TO_HANDLER_MAP.containsKey(cellText)) {
						m_indexToHandlerMap.put(m_tableCellCount, LABEL_TO_HANDLER_MAP.get(cellText));
					}
				}
			} else if (m_tableRowCount > 1) {
				// Table Body
				if ("tr".equals(localName)) {
					m_operationDetails.getPersonnel().add(m_currentPerson);
				} else if ("td".equals(localName)) {
					if (m_indexToHandlerMap.containsKey(m_tableCellCount)) {
						m_indexToHandlerMap.get(m_tableCellCount).setCellContent(m_currentPerson, cellText);
					}
				}
			}
		}
		if ("table".equals(localName)) {
			m_inTable = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		m_cellContentBuilder.append(ch, start, length);
	}

	@Override
	public boolean mayInterrupt() {
		return !m_inTable;
	}
}
