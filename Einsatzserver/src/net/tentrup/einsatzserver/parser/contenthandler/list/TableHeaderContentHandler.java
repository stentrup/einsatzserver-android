package net.tentrup.einsatzserver.parser.contenthandler.list;

import java.util.Map;

import net.tentrup.einsatzserver.parser.contenthandler.BaseContentHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class TableHeaderContentHandler extends BaseContentHandler {

	private final Map<String, OperationAwareContentHandler> m_columnHeaderToContentHandlerMap;
	private OperationAwareContentHandler m_bodyContentHandler = new NoOpContentHandler();
	private StringBuilder m_contentBuilder = new StringBuilder();

	public TableHeaderContentHandler(Map<String, OperationAwareContentHandler> columnHeaderToContentHandlerMap) {
		m_columnHeaderToContentHandlerMap = columnHeaderToContentHandlerMap;
	}

	public OperationAwareContentHandler getBodyContentHandler() {
		return m_bodyContentHandler;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("td".equals(localName)) {
			String headerLabel = m_contentBuilder.toString();
			if (m_columnHeaderToContentHandlerMap.containsKey(headerLabel)) {
				m_bodyContentHandler = m_columnHeaderToContentHandlerMap.get(headerLabel);
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String content = new String(ch, start, length);
		// filter line break
		content = content.replaceAll("\\n", "").replaceAll("\\r", "").replace("\u00a0"," ");
		m_contentBuilder.append(content);
	}
}
