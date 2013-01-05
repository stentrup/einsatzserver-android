package net.tentrup.einsatzserver.parser.contenthandler.list;

import static net.tentrup.einsatzserver.parser.ParseUtil.parseDate;
import static net.tentrup.einsatzserver.parser.ParseUtil.parseDateTime;
import static net.tentrup.einsatzserver.parser.ParseUtil.parseInt;
import static net.tentrup.einsatzserver.parser.ParseUtil.parseTime;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.Operation;

public class ColumnDefinitions {

	private static final Map<String, OperationAwareContentHandler> BASE_COLUMNS = new HashMap<String, OperationAwareContentHandler>();
	static {
		BASE_COLUMNS.put("Typ", new TextContentHandler() {
			@Override
			public void setTextContent(Operation operation, String[] content) {
				operation.setType(content[0]);
			}
		});
		BASE_COLUMNS.put("EK", new TextContentHandler() {
			@Override
			public void setTextContent(Operation operation, String[] content) {
				Pattern personnelCountPattern = Pattern.compile("(\\((\\d+)\\)\\s)?(\\d+)/(\\d+)");
				Matcher personnelCountMatcher = personnelCountPattern.matcher(content[0]);
				if (personnelCountMatcher.find()) {
					String personnelBookingRequested = personnelCountMatcher.group(2);
					operation.setPersonnelBookingRequested(parseInt(personnelBookingRequested));
					String personnelBookingConfirmed = personnelCountMatcher.group(3);
					operation.setPersonnelBookingConfirmed(parseInt(personnelBookingConfirmed));
					String personnelRequested = personnelCountMatcher.group(4);
					operation.setPersonnelRequested(parseInt(personnelRequested));
				}
			}
		});

		BASE_COLUMNS.put("Bezeichnung - Einsatzort", new TextContentHandler() {
			@Override
			public void setTextContent(Operation operation, String[] content) {
				if (content.length > 1) {
					operation.setDescription(content[0]);
					operation.setLocation(content[1]);
				} else {
					operation.setDescription(content[0]);
				}
			}
		});
	}

	public static final Map<String, OperationAwareContentHandler> ALL_OPERATIONS_COLUMNS = new HashMap<String, OperationAwareContentHandler>(BASE_COLUMNS);
	static {
		ALL_OPERATIONS_COLUMNS.put("Datum", new TextContentHandler() {
			@Override
			public void setTextContent(Operation operation, String[] content) {
				operation.setDate(parseDate(content[0]));
			}
		});
		ALL_OPERATIONS_COLUMNS.put("Zeit", new TextContentHandler() {
			@Override
			public void setTextContent(Operation operation, String[] content) {
				operation.setStartTime(parseTime(content[0]));
			}
		});
		ALL_OPERATIONS_COLUMNS.put("Letzte Bearbeitung", new TextContentHandler() {
			@Override
			public void setTextContent(Operation operation, String[] content) {
				if (content.length > 1) {
					operation.setLatestChangeAuthor(content[0]);
					operation.setLatestChangeDate(parseDateTime(content[1]));
				}
			}
		});
	}

	public static final Map<String, OperationAwareContentHandler> MY_OPERATIONS_COLUMNS = new HashMap<String, OperationAwareContentHandler>(BASE_COLUMNS);
	static {
		MY_OPERATIONS_COLUMNS.put("St.", new ImageAttributeContentHandler() {
			@Override
			public void setTitleContent(Operation operation, String title) {
				operation.setBookingState(BookingState.parseText(title));
			}
		});
		MY_OPERATIONS_COLUMNS.put("DatumZeit", new TextContentHandler() {
			@Override
			public void setTextContent(Operation operation, String[] content) {
				if (content.length > 1) {
					operation.setDate(parseDate(content[0]));
					operation.setStartTime(parseTime(content[1]));
				} else {
					operation.setDate(parseDate(content[0]));
				}
			}
		});
	}
}
