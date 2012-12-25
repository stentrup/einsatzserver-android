package net.tentrup.einsatzserver.parser.contenthandler.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.parser.contenthandler.BaseContentHandler;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class OperationsContentHandler extends BaseContentHandler {

	private boolean m_inTable = false;
	private int m_tableRowCount = 0;
	private int m_tableCellCount = 0;

	private Operation m_currentOperation;
	private final List<Operation> m_result;
	private final Map<String, OperationAwareContentHandler> m_columnHeaderToContentHandlerMap;
	private final Map<Integer, TableHeaderContentHandler> m_columnIndexToContentHandlerMap = new HashMap<Integer, TableHeaderContentHandler>();

	public OperationsContentHandler(List<Operation> result, Map<String, OperationAwareContentHandler> columnHeaderToContentHandlerMap) {
		m_result = result;
		m_columnIndexToContentHandlerMap.put(1, new TableHeaderContentHandler(null) {
			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				// do nothing
			}
			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				//do nothing
			}
			@Override
			public OperationAwareContentHandler getBodyContentHandler() {
				return new LinkContentHandler() {
					@Override
					public void setHrefContent(Operation operation, String href) {
						int startIndex = href.lastIndexOf("einsatz_id=");
						if (startIndex > 0) {
							startIndex = startIndex + "einsatz_id=".length();
							int endIndex = href.indexOf("&", startIndex);
							String idString;
							if (endIndex > 0) {
								idString = href.substring(startIndex, endIndex);
							} else {
								idString = href.substring(startIndex);
							}
							operation.setId(Integer.parseInt(idString));
						}
					}
				};
			}
		});
		m_columnHeaderToContentHandlerMap = columnHeaderToContentHandlerMap;

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
				m_currentOperation = new Operation();
			} else if ("td".equals(localName)) {
				m_tableCellCount++;
			} else {
				getContentHandler(m_tableRowCount, m_tableCellCount).startElement(uri, localName, qName, atts);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (m_inTable) {
			if ("tr".equals(localName)) {
				if (m_tableRowCount > 1) {
					m_result.add(m_currentOperation);
				}
			} else {
				getContentHandler(m_tableRowCount, m_tableCellCount).endElement(uri, localName, qName);
			}
		}
		if ("table".equals(localName)) {
			m_inTable = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		getContentHandler(m_tableRowCount, m_tableCellCount).characters(ch, start, length);
	}

	private ContentHandler getContentHandler(int tableRowIndex, int tableCellIndex) {
		if (tableRowIndex > 0) {
			if (tableRowIndex == 1) {
				// table header
				if (!m_columnIndexToContentHandlerMap.containsKey(tableCellIndex)) {
					m_columnIndexToContentHandlerMap.put(tableCellIndex, new TableHeaderContentHandler(m_columnHeaderToContentHandlerMap));
				}
				return m_columnIndexToContentHandlerMap.get(tableCellIndex);
			} else {
				// table body
				OperationAwareContentHandler contentHandler = m_columnIndexToContentHandlerMap.get(tableCellIndex).getBodyContentHandler();
				contentHandler.setOperation(m_currentOperation);
				return contentHandler;
			}
		}
		return new NoOpContentHandler();
	}
}
