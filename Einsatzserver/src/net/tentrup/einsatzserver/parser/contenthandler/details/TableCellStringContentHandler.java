package net.tentrup.einsatzserver.parser.contenthandler.details;

import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.parser.ParseUtil;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * {@link ContentHandler} for table cells (td) which contain strings.
 *
 * @author Tentrup
 *
 */
public abstract class TableCellStringContentHandler extends OperationDetailsBaseContentHandler {

	private int m_cellCount = 0;
	private StringBuilder m_cellContentBuilder = new StringBuilder();

	public TableCellStringContentHandler(OperationDetails operationDetails) {
		super(operationDetails);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if ("td".equals(localName)) {
			m_cellCount++;
			m_cellContentBuilder = new StringBuilder();
		}
		if ("br".equals(localName)) {
			m_cellContentBuilder.append(System.getProperty("line.separator"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("td".equals(localName)) {
			setCellContent(m_cellCount, ParseUtil.parseString(m_cellContentBuilder.toString()), m_operationDetails);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String content = new String(ch, start, length);
		// filter line break
		content = content.replaceAll("\\n", "").replaceAll("\\r", "");
		m_cellContentBuilder.append(content);
	}

	public abstract void setCellContent(int cellIndex, String content, OperationDetails operationDetails);

}
