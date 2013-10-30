package net.tentrup.einsatzserver.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.Person;
import net.tentrup.einsatzserver.model.Resource;
import net.tentrup.einsatzserver.model.ResultStateEnum;
import net.tentrup.einsatzserver.model.ResultWrapper;
import net.tentrup.einsatzserver.parser.contenthandler.details.OperationDetailsPageContentHandler;
import net.tentrup.einsatzserver.parser.contenthandler.list.AllOperationsPageContentHandler;
import net.tentrup.einsatzserver.parser.contenthandler.list.MyOperationsPageContentHandler;
import net.tentrup.einsatzserver.parser.contenthandler.list.OperationsPageContentHandler;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parser for Einsatzserver HTML Pages.
 * 
 * @author Tentrup
 * 
 */
public class HtmlParser {

	public ResultWrapper<List<Operation>> parseAllOperationsPage(String htmlContent) {
		OperationsPageContentHandler contentHandler = new AllOperationsPageContentHandler();
		return parseOperationsPage(htmlContent, contentHandler);
	}

	public ResultWrapper<List<Operation>> parseMyOperationsPage(String htmlContent) {
		OperationsPageContentHandler contentHandler = new MyOperationsPageContentHandler();
		return parseOperationsPage(htmlContent, contentHandler);
	}

	private ResultWrapper<List<Operation>> parseOperationsPage(String htmlContent, OperationsPageContentHandler contentHandler) {
		try {
			Parser parser = new Parser();
			parser.setContentHandler(contentHandler);
			parser.parse(new InputSource(new StringReader(htmlContent)));
		} catch (IOException e) {
			e.printStackTrace();
			return new ResultWrapper<List<Operation>>(null, ResultStateEnum.PARSE_ERROR);
		} catch (SAXException e) {
			e.printStackTrace();
			return new ResultWrapper<List<Operation>>(null, ResultStateEnum.PARSE_ERROR);
		}
		return new ResultWrapper<List<Operation>>(contentHandler.getResult(), ResultStateEnum.SUCCESSFUL);
	}

	public ResultWrapper<OperationDetails> parseOperationDetailsPage(Operation inputOperation, String htmlContent) {
		OperationDetails result = new OperationDetails();
		result.setId(inputOperation.getId());
		result.setLatestChangeAuthor(inputOperation.getLatestChangeAuthor());
		result.setLatestChangeDate(inputOperation.getLatestChangeDate());
		List<Person> personnel = new ArrayList<Person>();
		result.setPersonnel(personnel);
		result.setResources(new ArrayList<Resource>());
		try {
			//use TagSoup
			Parser parser = new Parser();
			parser.setContentHandler(new OperationDetailsPageContentHandler(result));
			parser.parse(new InputSource(new StringReader(htmlContent)));
			Collections.sort(personnel, new Comparator<Person>() {
				@Override
				public int compare(Person lhs, Person rhs) {
					if (lhs.getSurname() == null || rhs.getSurname() == null) {
						return 0;
					}
					int nameCompare = lhs.getSurname().compareTo(rhs.getSurname());
					if (nameCompare != 0) {
						return nameCompare;
					}
					if (lhs.getName() == null || rhs.getName() == null) {
						return 0;
					}
					return lhs.getName().compareTo(rhs.getName());
				}
			});
			int bookingRequestedCount = 0;
			for (Person person : personnel) {
				if (person.getBookingState() == BookingState.REQUESTED) {
					bookingRequestedCount++;
				}
			}
			result.setPersonnelBookingRequested(bookingRequestedCount);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResultWrapper<OperationDetails>(null, ResultStateEnum.PARSE_ERROR);
		} catch (SAXException e) {
			e.printStackTrace();
			return new ResultWrapper<OperationDetails>(null, ResultStateEnum.PARSE_ERROR);
		}
		return new ResultWrapper<OperationDetails>(result, ResultStateEnum.SUCCESSFUL);
	}
}
