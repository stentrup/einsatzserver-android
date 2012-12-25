package net.tentrup.einsatzserver.parser.contenthandler.details;

import java.util.HashMap;
import java.util.Map;

import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.parser.ParseUtil;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class OperationDetailsContentHandler extends OperationDetailsBaseContentHandler {

	private StringBuilder m_textBuilder = new StringBuilder();
	private OperationDetailsBaseContentHandler m_subHandler = null;

	private final Map<String, OperationDetailsBaseContentHandler> m_handlerMap;

	public OperationDetailsContentHandler(OperationDetails operationDetails) {
		super(operationDetails);
		m_handlerMap = new HashMap<String, OperationDetailsBaseContentHandler>();
		m_handlerMap.put("Bezeichnung", new TableCellStringContentHandler(operationDetails) {
			@Override
			public void setCellContent(int cellIndex, String content, OperationDetails operationDetails) {
				if (cellIndex == 1) {
					operationDetails.setDescription(content);
				}
			}
		});
		m_handlerMap.put("Einsatzort", new TableCellStringContentHandler(operationDetails) {
			@Override
			public void setCellContent(int cellIndex, String content, OperationDetails operationDetails) {
				if (cellIndex == 1) {
					operationDetails.setLocation(content);
				}
			}
		});
		m_handlerMap.put("Einsatzbeginn", new TableCellStringContentHandler(operationDetails) {
			@Override
			public void setCellContent(int cellIndex, String content, OperationDetails operationDetails) {
				switch (cellIndex) {
				case 1:
					operationDetails.setDate(ParseUtil.parseDate(content));
					break;
				case 3:
					operationDetails.setStartTime(ParseUtil.parseTime(content));
					break;
				}	
			}
		});
		m_handlerMap.put("Einsatzende", new TableCellStringContentHandler(operationDetails) {
			@Override
			public void setCellContent(int cellIndex, String content, OperationDetails operationDetails) {
				switch (cellIndex) {
				case 1:
					operationDetails.setEndDate(ParseUtil.parseDate(content));
					break;
				case 3:
					operationDetails.setEndTime(ParseUtil.parseTime(content));
					break;
				}	
			}
		});
		m_handlerMap.put("Meldeort", new TableCellStringContentHandler(operationDetails) {
			@Override
			public void setCellContent(int cellIndex, String content, OperationDetails operationDetails) {
				if (cellIndex == 1) {
					operationDetails.setReportLocation(content);
				}
			}
		});
		m_handlerMap.put("Meldezeit", new TableCellStringContentHandler(operationDetails) {
			@Override
			public void setCellContent(int cellIndex, String content, OperationDetails operationDetails) {
				switch (cellIndex) {
				case 1:
					operationDetails.setReportDate(ParseUtil.parseDate(content));
					break;
				case 3:
					operationDetails.setReportTime(ParseUtil.parseTime(content));
					break;
				}	
			}
		});
		m_handlerMap.put("Einsatzkräfteanforderung:", new TableCellStringContentHandler(operationDetails) {
			@Override
			public void setCellContent(int cellIndex, String content, OperationDetails operationDetails) {
				switch (cellIndex) {
				case 1:
					operationDetails.setPersonnelRequested(Integer.parseInt(content));
					break;
				case 3:
					operationDetails.setPersonnelBookingConfirmed(Integer.parseInt(content));
					break;
				}	
			}
		});
		m_handlerMap.put("Verpflegungwird gestellt:", new TableCellInputContentHandler(operationDetails));
		m_handlerMap.put("Bemerkung", new TableCellStringContentHandler(operationDetails) {
			@Override
			public void setCellContent(int cellIndex, String content, OperationDetails operationDetails) {
				if (cellIndex == 1) {
					operationDetails.setComment(content);
				}
			}
		});
		m_handlerMap.put("Ansprechpartner", new TableCellStringContentHandler(operationDetails) {
			@Override
			public void setCellContent(int cellIndex, String content, OperationDetails operationDetails) {
				if (cellIndex == 1) {
					operationDetails.setContactPerson(content);
				}
			}
		});
		m_handlerMap.put("Rufnummer", new TableCellStringContentHandler(operationDetails) {
			@Override
			public void setCellContent(int cellIndex, String content, OperationDetails operationDetails) {
				if (cellIndex == 1) {
					operationDetails.setContactPersonPhone(content);
				}
			}
		});
		m_handlerMap.put("Einsatzkräfte", new PersonnelTableContentHandler(operationDetails));
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (m_subHandler != null) {
			m_subHandler.startElement(uri, localName, qName, atts);
		}
		if ("b".equals(localName)) {
			m_textBuilder = new StringBuilder();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (m_subHandler != null) {
			m_subHandler.endElement(uri, localName, qName);
		}
		if ("b".equals(localName)) {
			String label = m_textBuilder.toString();
			if (m_handlerMap.containsKey(label) && (m_subHandler == null || m_subHandler.mayInterrupt())) {
				m_subHandler = m_handlerMap.get(label);
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (m_subHandler != null) {
			m_subHandler.characters(ch, start, length);
		}
		m_textBuilder.append(ch, start, length);
	}
}
