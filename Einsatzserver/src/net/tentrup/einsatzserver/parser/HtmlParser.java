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
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.joda.time.LocalDate;
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
 */
public class HtmlParser {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");

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
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode node = cleaner.clean(htmlContent);
		String expression = "//div[@id='rechtesFenster']/table/tbody";
		try {
			Object[] evaluateXPath = node.evaluateXPath(expression);
			if (evaluateXPath == null || evaluateXPath.length < 1) {
				return new ResultWrapper<List<Operation>>(null, ResultStateEnum.PARSE_ERROR);
			}
			TagNode tbody = (TagNode) evaluateXPath[0];
			
			TagNode[] rows = tbody.getAllElements(false);
			for (int i = 1; i < rows.length; i++) {
				Operation operation = new Operation();
				result.add(operation);
				TagNode[] cells = rows[i].getAllElements(false);
				TagNode idNode = cells[0];
				TagNode idANode = idNode.getAllElements(false)[0];
				String href = idANode.getAttributeByName("href");
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
				TagNode typeNode = cells[1];
				operation.setType(typeNode.getText().toString());
				TagNode personnelCountNode = cells[3];
				Pattern personnelCountPattern = Pattern.compile("(\\((\\d+)\\)\\s)?(\\d+)/(\\d+)");
				Matcher personnelCountMatcher = personnelCountPattern.matcher(personnelCountNode.getText().toString());
				if (personnelCountMatcher.find()) {
					String personnelBookingRequested = personnelCountMatcher.group(2);
					operation.setPersonnelBookingRequested(parseInt(personnelBookingRequested));
					String personnelBookingConfirmed = personnelCountMatcher.group(3);
					operation.setPersonnelBookingConfirmed(parseInt(personnelBookingConfirmed));
					String personnelRequested = personnelCountMatcher.group(4);
					operation.setPersonnelRequested(parseInt(personnelRequested));
				}
				TagNode datumNode = cells[5];
				operation.setDate(parseDate(datumNode.getText().toString()));
				TagNode uhrzeitNode = cells[6];
				List uhrzeitNodeChildren = uhrzeitNode.getChildren();
				if (uhrzeitNodeChildren.size() > 2) {
					Object uhrzeitVonNode = uhrzeitNodeChildren.get(0);
					operation.setStartTime(parseTime(uhrzeitVonNode.toString()));
				} else {
					String startTime = uhrzeitNode.getText().toString();
					if (startTime.trim().length() > 0) {
						operation.setStartTime(parseTime(startTime));
					}
				}
				TagNode infoNode = cells[7];
				List infoNodeChildren = infoNode.getChildren();
				if (infoNodeChildren.size() > 2) {
					Object beschreibungNode = infoNodeChildren.get(0);
					Object ortNode = infoNodeChildren.get(2);
					operation.setDescription(beschreibungNode.toString());
					operation.setLocation(ortNode.toString());
				} else {
					operation.setDescription(infoNode.getText().toString());
				}
			}
		} catch (XPatherException e) {
			e.printStackTrace();
			return new ResultWrapper<List<Operation>>(null, ResultStateEnum.PARSE_ERROR);
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

	public ResultWrapper<OperationDetails> parseOperationDetailsPage(int operationId, String htmlContent) {
		OperationDetails result = new OperationDetails();
		result.setId(operationId);
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
				String name = getStringForXpath(node, "td[3]");
				String surname = getStringForXpath(node, "td[4]");
				String bookingStateString = getStringForXpath(node, "td[1]");
				BookingState bookingState = BookingState.parseShortText(bookingStateString);
				Person person = new Person();
				person.setName(name);
				person.setSurname(surname);
				person.setBookingState(bookingState);
				personnel.add(person);
			}
			Collections.sort(personnel, new Comparator<Person>() {
				@Override
				public int compare(Person lhs, Person rhs) {
					if (lhs.getName() == null || rhs.getName() == null) {
						return 0;
					}
					int nameCompare = lhs.getName().compareTo(rhs.getName());
					if (nameCompare != 0) {
						return nameCompare;
					}
					if (lhs.getSurname() == null || rhs.getSurname() == null) {
						return 0;
					}
					return lhs.getSurname().compareTo(rhs.getSurname());
				}
			});
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

	private String getStringForXpath(Node root, String xpathExpression) throws XPatherException, XPathExpressionException, ParserConfigurationException {
		Node result = getNodeForXpath(root, xpathExpression);
		if (result == null) {
			return null;
		}
		String resultText = result.getTextContent();
		if (resultText == null || resultText.trim().length() < 1) {
			return null;
		}
		resultText = resultText.replaceAll("\\r\\n|\\r|\\n", System.getProperty("line.separator"));
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
