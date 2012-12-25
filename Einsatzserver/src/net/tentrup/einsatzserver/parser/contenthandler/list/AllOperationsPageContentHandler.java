package net.tentrup.einsatzserver.parser.contenthandler.list;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AllOperationsPageContentHandler extends OperationsPageContentHandler {

	public AllOperationsPageContentHandler() {
		super("div");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		super.startElement(uri, localName, qName, atts);
		if ("div".equals(localName) && "rechtesFenster".equals(atts.getValue("id"))) {
			m_subContentHandler = new OperationsContentHandler(getResult(), net.tentrup.einsatzserver.parser.contenthandler.list.ColumnDefinitions.ALL_OPERATIONS_COLUMNS);
		}
	}

}
