package net.tentrup.einsatzserver.parser.contenthandler.details;

import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.parser.contenthandler.PageContentHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class OperationDetailsPageContentHandler extends PageContentHandler {

	private final OperationDetails m_result;

	public OperationDetailsPageContentHandler(OperationDetails result) {
		super("div");
		m_result = result;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		super.startElement(uri, localName, qName, atts);
		if ("div".equals(localName) && "rechtesFenster".equals(atts.getValue("id"))) {
			m_subContentHandler = new OperationDetailsContentHandler(m_result);
		}
	}

	@Override
	protected void setUsername(String username) {
		m_result.setUsername(username);
	}
}
