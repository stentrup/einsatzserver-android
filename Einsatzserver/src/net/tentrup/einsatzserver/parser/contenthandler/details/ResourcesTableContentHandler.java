package net.tentrup.einsatzserver.parser.contenthandler.details;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.ContentHandler;

import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.Resource;

/**
 * {@link ContentHandler} for the HTML table which contains the operation resources.
 *
 * @author Tentrup
 *
 */
public class ResourcesTableContentHandler extends OperationDetailsTableContentHandler<Resource> {

	public ResourcesTableContentHandler(OperationDetails operationDetails) {
		super(operationDetails);
	}

	private static final Map<String, TableCellContentHandler<Resource>> LABEL_TO_HANDLER_MAP = new HashMap<String, TableCellContentHandler<Resource>>();
	static {
		LABEL_TO_HANDLER_MAP.put("Einsatzmittel", new TableCellContentHandler<Resource>() {
			@Override
			public void setCellContent(Resource resource, String content) {
				resource.setName(content);
			}
		});
	}

	@Override
	public Map<String, TableCellContentHandler<Resource>> getLabelToHandlerMap() {
		return LABEL_TO_HANDLER_MAP;
	}

	@Override
	public Resource createNewItem() {
		return new Resource();
	}

	@Override
	public List<Resource> getItemList() {
		return m_operationDetails.getResources();
	}
}
