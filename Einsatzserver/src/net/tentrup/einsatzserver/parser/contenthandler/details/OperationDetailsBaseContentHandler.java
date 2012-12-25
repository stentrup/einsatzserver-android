package net.tentrup.einsatzserver.parser.contenthandler.details;

import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.parser.contenthandler.BaseContentHandler;

public abstract class OperationDetailsBaseContentHandler extends BaseContentHandler {

	protected final OperationDetails m_operationDetails;

	public OperationDetailsBaseContentHandler(OperationDetails operationDetails) {
		m_operationDetails = operationDetails;
	}

	public boolean mayInterrupt() {
		return true;
	}
}
