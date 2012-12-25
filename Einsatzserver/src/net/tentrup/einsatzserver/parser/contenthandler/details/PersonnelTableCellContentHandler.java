package net.tentrup.einsatzserver.parser.contenthandler.details;

import net.tentrup.einsatzserver.model.Person;

public interface PersonnelTableCellContentHandler {
	void setCellContent(Person person, String content);
}
