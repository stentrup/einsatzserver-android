package net.tentrup.einsatzserver.parser.contenthandler.list;

import java.util.ArrayList;
import java.util.List;

import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.parser.contenthandler.PageContentHandler;

public abstract class OperationsPageContentHandler extends PageContentHandler {

	private final List<Operation> m_result = new ArrayList<Operation>();

	public OperationsPageContentHandler(String tagName) {
		super(tagName);
	}

	public List<Operation> getResult() {
		return m_result;
	}

	@Override
	protected void setUsername(String username) {
		// not used in operations list
	}
}
