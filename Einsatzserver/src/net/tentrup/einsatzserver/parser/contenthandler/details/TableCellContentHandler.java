package net.tentrup.einsatzserver.parser.contenthandler.details;


public interface TableCellContentHandler<T> {
	void setCellContent(T targetObject, String content);
}
