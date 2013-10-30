package net.tentrup.einsatzserver.parser.contenthandler.details;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.parser.ParseUtil;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * {@link ContentHandler} for HTML tables on the operation details page.
 *
 * @author Tentrup
 *
 */
public abstract class OperationDetailsTableContentHandler<T> extends OperationDetailsBaseContentHandler {

	public abstract Map<String, TableCellContentHandler<T>> getLabelToHandlerMap();

	public abstract T createNewItem();

	public abstract List<T> getItemList();

	private boolean m_inTable = false;
	private boolean m_finished = false;
	private int m_tableRowCount = 0;
	private int m_tableCellCount = 0;

	private StringBuilder m_cellContentBuilder = new StringBuilder();
	private T m_currentItem;

	private final Map<Integer, TableCellContentHandler<T>> m_indexToHandlerMap = new HashMap<Integer, TableCellContentHandler<T>>();

	public OperationDetailsTableContentHandler(OperationDetails operationDetails) {
		super(operationDetails);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if ("table".equals(localName)) {
			m_inTable = !m_finished;
		}
		if (m_inTable) {
			if ("tr".equals(localName)) {
				m_tableRowCount++;
				m_tableCellCount = 0;
				m_currentItem = createNewItem();
			} else if ("td".equals(localName)) {
				m_tableCellCount++;
				m_cellContentBuilder = new StringBuilder();
			} else if ("br".equals(localName)) {
				m_cellContentBuilder.append(System.getProperty("line.separator"));
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
					if (getLabelToHandlerMap().containsKey(cellText)) {
						m_indexToHandlerMap.put(m_tableCellCount, getLabelToHandlerMap().get(cellText));
					}
				}
			} else if (m_tableRowCount > 1) {
				// Table Body
				if ("tr".equals(localName)) {
					getItemList().add(m_currentItem);
				} else if ("td".equals(localName)) {
					if (m_indexToHandlerMap.containsKey(m_tableCellCount)) {
						m_indexToHandlerMap.get(m_tableCellCount).setCellContent(m_currentItem, cellText);
					}
				}
			}
		}
		if ("table".equals(localName)) {
			m_finished = m_inTable;
			m_inTable = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		m_cellContentBuilder.append(ch, start, length);
	}

	@Override
	public boolean mayInterrupt() {
		return !m_inTable || m_finished;
	}

}
