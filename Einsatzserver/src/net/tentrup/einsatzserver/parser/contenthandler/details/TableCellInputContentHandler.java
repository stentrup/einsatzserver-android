package net.tentrup.einsatzserver.parser.contenthandler.details;

import net.tentrup.einsatzserver.model.OperationDetails;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * {@link ContentHandler} for table cell which contains a checkbox (HTML input)
 *
 * @author Tentrup
 *
 */
public class TableCellInputContentHandler extends OperationDetailsBaseContentHandler {

	public TableCellInputContentHandler(OperationDetails operationDetails) {
		super(operationDetails);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if ("input".equals(localName)) {
			String checked = atts.getValue("checked");
			m_operationDetails.setCatering(checked != null);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}

}
