package net.tentrup.einsatzserver.parser.contenthandler.list;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class NoOpContentHandler extends OperationAwareContentHandler {

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}

}
