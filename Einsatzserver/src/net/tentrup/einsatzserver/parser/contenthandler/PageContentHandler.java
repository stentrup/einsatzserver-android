package net.tentrup.einsatzserver.parser.contenthandler;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class PageContentHandler extends BaseContentHandler {

	protected ContentHandler m_subContentHandler = null;
	private final String m_tagName;
	private int m_tagCount = 0;
	private final Stack<String> m_tagStack = new Stack<String>();

	public PageContentHandler(String tagName) {
		m_tagName = tagName;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		m_tagStack.push(localName);
		if (m_subContentHandler != null) {
			m_subContentHandler.startElement(uri, localName, qName, atts);
		}
		if (m_tagName.equals(localName)) {
			m_tagCount++;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		m_tagStack.pop();
		if (m_subContentHandler != null) {
			m_subContentHandler.endElement(uri, localName, qName);
		}
		if (m_tagName.equals(localName)) {
			m_tagCount--;
			if (m_tagCount == 0) {
				m_subContentHandler = null;
			}
		}	
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String content = new String(ch, start, length);
		content = content.replaceAll("\\n", "").replaceAll("\\r", "").trim();
		m_tagStack.toString();
		if (("body".equals(m_tagStack.peek()) || "div".equals(m_tagStack.peek())) && content.startsWith("Name: ")) {
			String username = content.substring("Name: ".length());
			setUsername(username);
		}
		if (m_subContentHandler != null) {
			m_subContentHandler.characters(ch, start, length);
		}
	}

	protected abstract void setUsername(String username);
}
