package net.tentrup.einsatzserver.parser.contenthandler.list;

import net.tentrup.einsatzserver.model.Operation;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class LinkContentHandler extends OperationAwareContentHandler {

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if ("a".equals(localName)) {
			String href = atts.getValue("href");
			if (href != null) {
				setHrefContent(getOperation(), href);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}

	public abstract void setHrefContent(Operation operation, String link);

}
