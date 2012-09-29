package net.tentrup.einsatzserver.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.Person;
import net.tentrup.einsatzserver.model.ResultStateEnum;
import net.tentrup.einsatzserver.model.ResultWrapper;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.XPatherException;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parser for Einsatzserver HTML Pages.
 * 
 * @author Tentrup
 * 
 * TODO Gemeinsamkeiten der Methoden parseAllOperations und parseMyOperations zusammenfassen
 */
public class HtmlParser {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");

	private String parseName(String htmlContent) {
		try {
			Document root = new DomSerializer(new CleanerProperties()).createDOM(new HtmlCleaner().clean(htmlContent));
			XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//div[@id='linkesFenster']");
			Node tbody = (Node) xpath.evaluate(root, XPathConstants.NODE);
			NodeList childNodes = tbody.getChildNodes();
			return getNameFromChildNodes(childNodes);
		} catch (ParserConfigurationException exc) {
			return null;
		} catch (XPathExpressionException exc) {
			return null;
		}
	}

	private String getNameFromChildNodes(NodeList childNodes) {
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			String nodeValue = child.getNodeValue();
			if (nodeValue != null) {
				nodeValue = nodeValue.trim();
				if (nodeValue.startsWith("Name: ")) {
					return nodeValue.substring("Name: ".length());
				}
			}
			getNameFromChildNodes(child.getChildNodes());
		}
		return null;
	}

	public ResultWrapper<List<Operation>> parseAllOperationsPage(String htmlContent) {
		List<Operation> result = new ArrayList<Operation>();
		try {
			Document root = new DomSerializer(new CleanerProperties()).createDOM(new HtmlCleaner().clean(htmlContent));
			XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//div[@id='rechtesFenster']/table/tbody");
			Node tbody = (Node) xpath.evaluate(root, XPathConstants.NODE);
			if (tbody == null) {
				return new ResultWrapper<List<Operation>>(result, ResultStateEnum.SUCCESSFUL);
			}
			NodeList rows = tbody.getChildNodes();

			for (int i = 1; i < rows.getLength(); i++) {
				Operation operation = new Operation();
				result.add(operation);
				NodeList cells = rows.item(i).getChildNodes();
				Node idNode = cells.item(0);
				Node idANode = idNode.getChildNodes().item(0);
				String href = idANode.getAttributes().getNamedItem("href").getTextContent();
				// einsatz_uebersicht.php?einsatz_id=1234
				int startIndex = href.lastIndexOf("einsatz_id=");
				if (startIndex > 0) {
					startIndex = startIndex + "einsatz_id=".length();
					int endIndex = href.indexOf("&", startIndex);
					String idString;
					if (endIndex > 0) {
						idString = href.substring(startIndex, endIndex);
					} else {
						idString = href.substring(startIndex);
					}
					operation.setId(Integer.parseInt(idString));
				}
				Node typeNode = cells.item(1);
				operation.setType(typeNode.getTextContent());
				Node personnelCountNode = cells.item(3);
				Pattern personnelCountPattern = Pattern.compile("(\\((\\d+)\\)\\s)?(\\d+)/(\\d+)");
				Matcher personnelCountMatcher = personnelCountPattern.matcher(personnelCountNode.getTextContent());
				if (personnelCountMatcher.find()) {
					String personnelBookingRequested = personnelCountMatcher.group(2);
					operation.setPersonnelBookingRequested(parseInt(personnelBookingRequested));
					String personnelBookingConfirmed = personnelCountMatcher.group(3);
					operation.setPersonnelBookingConfirmed(parseInt(personnelBookingConfirmed));
					String personnelRequested = personnelCountMatcher.group(4);
					operation.setPersonnelRequested(parseInt(personnelRequested));
				}
				Node datumNode = cells.item(5);
				operation.setDate(parseDate(datumNode.getTextContent()));
				Node uhrzeitNode = cells.item(6);
				NodeList uhrzeitNodeChildren = uhrzeitNode.getChildNodes();
				if (uhrzeitNodeChildren.getLength() > 2) {
					Node uhrzeitVonNode = uhrzeitNodeChildren.item(0);
					operation.setStartTime(parseTime(uhrzeitVonNode.getTextContent()));
				} else {
					String startTime = uhrzeitNode.getTextContent();
					if (startTime.trim().length() > 0) {
						operation.setStartTime(parseTime(startTime));
					}
				}
				Node infoNode = cells.item(7);
				NodeList infoNodeChildren = infoNode.getChildNodes();
				if (infoNodeChildren.getLength() > 2) {
					Node beschreibungNode = infoNodeChildren.item(0);
					Node ortNode = infoNodeChildren.item(2);
					operation.setDescription(beschreibungNode.getTextContent());
					operation.setLocation(ortNode.getTextContent());
				} else {
					operation.setDescription(infoNode.getTextContent());
				}
				Node latestChangeNode = cells.item(8);
				NodeList latestChangeNodeChildren = latestChangeNode.getChildNodes();
				if (latestChangeNodeChildren.getLength() > 2) {
					operation.setLatestChangeAuthor(latestChangeNodeChildren.item(0).getTextContent());
					String string = latestChangeNodeChildren.item(2).getTextContent();
					String parseString = parseString(string);
					LocalDateTime parseDateTime = parseDateTime(parseString);
					operation.setLatestChangeDate(parseDateTime);
				}
			}
		} catch (XPathException exc) {
			exc.printStackTrace();
			return new ResultWrapper<List<Operation>>(null, ResultStateEnum.PARSE_ERROR);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			new ResultWrapper<List<Operation>>(null, ResultStateEnum.PARSE_ERROR);
		}
		return new ResultWrapper<List<Operation>>(result, ResultStateEnum.SUCCESSFUL);
	}

	public ResultWrapper<List<Operation>> parseMyOperationsPage(String htmlContent) {
		List<Operation> result = new ArrayList<Operation>();
		try {
			Document root = new DomSerializer(new CleanerProperties()).createDOM(new HtmlCleaner().clean(htmlContent));
			XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//div[@id='rechtesFenster']/b[text()=\"Veranstaltungen, bei denen ich eingesetzt oder vorgemerkt bin:\"]/following-sibling::table/tbody");
			Node tbody = (Node) xpath.evaluate(root, XPathConstants.NODE);
			if (tbody == null) {
				return new ResultWrapper<List<Operation>>(result, ResultStateEnum.SUCCESSFUL);
			}
			NodeList rows = tbody.getChildNodes();
			for (int i = 1; i < rows.getLength(); i++) {
				Operation operation = new Operation();
				result.add(operation);
				NodeList cells = rows.item(i).getChildNodes();
				Node idNode = cells.item(0);
				Node idANode = idNode.getChildNodes().item(0);
				String href = idANode.getAttributes().getNamedItem("href").getTextContent();
				int startIndex = href.lastIndexOf("einsatz_id=");
				if (startIndex > 0) {
					startIndex = startIndex + "einsatz_id=".length();
					int endIndex = href.indexOf("&", startIndex);
					String idString;
					if (endIndex > 0) {
						idString = href.substring(startIndex, endIndex);
					} else {
						idString = href.substring(startIndex);
					}
					operation.setId(Integer.parseInt(idString));
				}
				Node typeNode = cells.item(1);
				operation.setType(typeNode.getTextContent());
				Node statusNode = cells.item(2);
				Node statusImgNode = statusNode.getChildNodes().item(0);
				String status = statusImgNode.getAttributes().getNamedItem("title").getTextContent();
				operation.setBookingState(BookingState.parseText(status));
				Node datumUhrzeitNode = cells.item(5);
				NodeList datumUhrzeitNodeChildren = datumUhrzeitNode.getChildNodes();
				if (datumUhrzeitNodeChildren.getLength() > 2) {
					Node datumNode = datumUhrzeitNodeChildren.item(0);
					Node uhrzeitVonNode = datumUhrzeitNodeChildren.item(2);
					operation.setDate(parseDate(datumNode.getTextContent()));
					operation.setStartTime(parseTime(uhrzeitVonNode.getTextContent()));
				} else {
					operation.setDate(parseDate(datumUhrzeitNode.getTextContent()));
				}
				Node infoNode = cells.item(7);
				NodeList infoNodeChildren = infoNode.getChildNodes();
				if (infoNodeChildren.getLength() > 2) {
					Node beschreibungNode = infoNodeChildren.item(0);
					Node ortNode = infoNodeChildren.item(2);
					operation.setDescription(beschreibungNode.getTextContent());
					operation.setLocation(ortNode.getTextContent());
				} else {
					operation.setDescription(infoNode.getTextContent());
				}
			}
		} catch (XPathException exc) {
			exc.printStackTrace();
			return new ResultWrapper<List<Operation>>(null, ResultStateEnum.PARSE_ERROR);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			new ResultWrapper<List<Operation>>(null, ResultStateEnum.PARSE_ERROR);
		}
		return new ResultWrapper<List<Operation>>(result, ResultStateEnum.SUCCESSFUL);
	}

	public ResultWrapper<OperationDetails> parseOperationDetailsPage(Operation inputOperation, String htmlContent) {
		OperationDetails result = new OperationDetails();
		result.setId(inputOperation.getId());
		result.setLatestChangeAuthor(inputOperation.getLatestChangeAuthor());
		result.setLatestChangeDate(inputOperation.getLatestChangeDate());
		try {
			Document root = new DomSerializer(new CleanerProperties()).createDOM(new HtmlCleaner().clean(htmlContent));
			result.setDescription(getStringForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"Bezeichnung\"]/../../td[2]"));
			result.setLocation(getStringForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"Einsatzort\"]/../../td[2]"));
			result.setDate(parseDate(getStringForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"Einsatzbeginn\"]/../../td[2]")));
			result.setStartTime(parseTime(getStringForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"Einsatzbeginn\"]/../../td[4]")));
			result.setEndDate(parseDate(getStringForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"Einsatzende\"]/../../td[2]")));
			result.setEndTime(parseTime(getStringForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"Einsatzende\"]/../../td[4]")));
			result.setReportLocation(getStringForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"Meldeort\"]/../../td[2]"));
			result.setReportDate(parseDate(getStringForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"Meldezeit\"]/../../td[2]")));
			result.setReportTime(parseTime(getStringForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"Meldezeit\"]/../../td[4]")));;
			result.setPersonnelRequested(getIntForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"anforderung:\"]/../../td[2]"));
			result.setPersonnelBookingConfirmed(getIntForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"anforderung:\"]/../../td[4]"));
			result.setCatering(getBooleanForXpath(root, "//div[@id='rechtesFenster']//b[contains(., 'Verpflegung')]/../../td[4]/input/@checked"));
			result.setComment(getStringForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"Bemerkung\"]/../../td[2]"));
			List<Person> personnel = new ArrayList<Person>();
			result.setPersonnel(personnel);
			NodeList nodeList = getNodeListForXpath(root, "//div[@id='rechtesFenster']//b[text()=\"Dienststunden:\"]/../../../../following-sibling::table/tbody/tr");
			for (int i = 1; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String surname = getStringForXpath(node, "td[3]");
				String name = getStringForXpath(node, "td[4]");
				String qualification = getStringForXpath(node, "td[5]");
				String bookingStateString = getStringForXpath(node, "td[1]");
				BookingState bookingState = BookingState.parseShortText(bookingStateString);
				String comment = getStringForXpath(node, "td[9]");
				String division = getStringForXpath(node, "td[2]");
				LocalTime startTime = parseTime(getStringForXpath(node, "td[6]"));
				LocalTime endTime = parseTime(getStringForXpath(node, "td[7]"));
				Person person = new Person();
				person.setName(name);
				person.setSurname(surname);
				person.setBookingState(bookingState);
				person.setComment(comment);
				person.setDivision(division);
				person.setStartTime(startTime);
				person.setEndTime(endTime);
				person.setQualification(qualification);
				personnel.add(person);
			}
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
		} catch (XPatherException e) {
			e.printStackTrace();
			return new ResultWrapper<OperationDetails>(null, ResultStateEnum.PARSE_ERROR);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return new ResultWrapper<OperationDetails>(null, ResultStateEnum.PARSE_ERROR);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return new ResultWrapper<OperationDetails>(null, ResultStateEnum.PARSE_ERROR);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return new ResultWrapper<OperationDetails>(null, ResultStateEnum.PARSE_ERROR);
		}
		return new ResultWrapper<OperationDetails>(result, ResultStateEnum.SUCCESSFUL, parseName(htmlContent));
	}

	private LocalDate parseDate(String dateString) {
		if (dateString == null) {
			return null;
		}
		return DATE_FORMATTER.parseLocalDate(dateString);
	}

	private LocalTime parseTime(String timeString) {
		if (timeString == null) {
			return null;
		}
		return TIME_FORMATTER.parseLocalTime(timeString);
	}

	private LocalDateTime parseDateTime(String dateTimeString) {
		if (dateTimeString == null) {
			return null;
		}
		return DATE_TIME_FORMATTER.parseLocalDateTime(dateTimeString);
	}

	private String getStringForXpath(Node root, String xpathExpression) throws XPatherException, XPathExpressionException, ParserConfigurationException {
		Node result = getNodeForXpath(root, xpathExpression);
		if (result == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		concatTextFromNode(builder, result.getFirstChild());
		String resultText = parseString(builder.toString());
		return resultText;
	}

	private void concatTextFromNode(StringBuilder builder, Node node) {
		if (node != null) {
			if ("#text".equals(node.getNodeName())) {
				builder.append(removeLineSeparators(node.getTextContent()));
			} else if ("br".equals(node.getNodeName())) {
				builder.append(System.getProperty("line.separator"));
			}
			concatTextFromNode(builder, node.getNextSibling());
		}
	}

	private String removeLineSeparators(String inputString) {
		return inputString.replaceAll("\\r\\n|\\r|\\n", "");
	}

	private String parseString(String inputString) {
		String resultText = inputString;
		resultText = inputString.replace("\u00a0", " ");
		if (resultText.trim().length() < 1) {
			return null;
		}
		return resultText;
	}

	private boolean getBooleanForXpath(Node root, String xpathExpression) throws XPatherException, XPathExpressionException, ParserConfigurationException {
		Node result = getNodeForXpath(root, xpathExpression);
		return result != null;
	}

	private int getIntForXpath(Node root, String xpathExpression) throws XPathExpressionException, XPatherException, ParserConfigurationException {
		String result = getStringForXpath(root, xpathExpression);
		int resultInt = Integer.parseInt(result);
		return resultInt;
	}

	private Node getNodeForXpath(Node root, String xpathExpression) throws ParserConfigurationException, XPathExpressionException {
		XPathExpression xpath = XPathFactory.newInstance().newXPath().compile(xpathExpression);
		Node node = (Node) xpath.evaluate(root, XPathConstants.NODE);
		return node;
	}

	private NodeList getNodeListForXpath(Node root, String xpathExpression) throws ParserConfigurationException, XPathExpressionException {
		XPathExpression xpath = XPathFactory.newInstance().newXPath().compile(xpathExpression);
		NodeList node = (NodeList) xpath.evaluate(root, XPathConstants.NODESET);
		return node;
	}

	private int parseInt(String intValue) {
		if (intValue == null) {
			return 0;
		}
		return Integer.parseInt(intValue);
	}
}
