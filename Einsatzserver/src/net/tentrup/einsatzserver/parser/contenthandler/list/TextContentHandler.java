package net.tentrup.einsatzserver.parser.contenthandler.list;

import java.util.ArrayList;
import java.util.List;

import net.tentrup.einsatzserver.model.Operation;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class TextContentHandler extends OperationAwareContentHandler {

	private StringBuilder m_contentBuilder = new StringBuilder();
	private List<String> m_contentList = new ArrayList<String>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("br".equals(localName) || "td".equals(localName)) {
			m_contentList.add(m_contentBuilder.toString());
			m_contentBuilder = new StringBuilder();
			if ("td".equals(localName)) {
				setTextContent(getOperation(), m_contentList.toArray(new String[m_contentList.size()]));
				m_contentList.clear();
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

	public abstract void setTextContent(Operation operation, String[] content);
}
