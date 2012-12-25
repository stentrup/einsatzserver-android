package net.tentrup.einsatzserver.parser.contenthandler.list;

import net.tentrup.einsatzserver.model.Operation;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class ImageAttributeContentHandler extends OperationAwareContentHandler {

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if ("img".equals(localName)) {
			String title = atts.getValue("title");
			if (title != null) {
				setTitleContent(getOperation(), title);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}

	public abstract void setTitleContent(Operation operation, String title);
}
