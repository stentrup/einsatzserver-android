package net.tentrup.einsatzserver.parser.contenthandler.list;

import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.parser.contenthandler.BaseContentHandler;

public abstract class OperationAwareContentHandler extends BaseContentHandler {

	private Operation m_operation;

	public Operation getOperation() {
		return m_operation;
	}

	public void setOperation(Operation operation) {
		m_operation = operation;
	}
}
