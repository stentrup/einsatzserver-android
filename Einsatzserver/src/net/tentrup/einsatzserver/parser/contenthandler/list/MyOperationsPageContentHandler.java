package net.tentrup.einsatzserver.parser.contenthandler.list;


import org.xml.sax.SAXException;

public class MyOperationsPageContentHandler extends OperationsPageContentHandler {

	private StringBuilder m_contentBuilder = new StringBuilder();

	public MyOperationsPageContentHandler() {
		super("table");
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (m_subContentHandler == null) {
			String text = m_contentBuilder.toString();
			if (text.equals("Veranstaltungen, bei denen ich eingesetzt oder vorgemerkt bin:")) {
				m_subContentHandler = new OperationsContentHandler(getResult(), net.tentrup.einsatzserver.parser.contenthandler.list.ColumnDefinitions.MY_OPERATIONS_COLUMNS);
			}
			m_contentBuilder = new StringBuilder();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		if (m_subContentHandler == null) {
			String content = new String(ch, start, length);
			// filter line break
			content = content.replaceAll("\\n", "").replaceAll("\\r", "").replace("\u00a0"," ");
			m_contentBuilder.append(content);
		}
	}

}
