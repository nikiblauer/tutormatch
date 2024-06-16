package at.ac.tuwien.sepr.groupphase.backend.tiss;

import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code SubjectProcessor} class provides methods to parse course response
 * XML strings and convert them into lists of {@code Subject} objects.
 *
 * <p>This class includes methods to:
 * <ul>
 *     <li>Convert an XML string to a Document object</li>
 *     <li>Extract subjects from the Document object</li>
 *     <li>Retrieve text content of specific XML elements</li>
 * </ul>
 */
public class SubjectProcessor {

    /**
     * Parses a course response XML string and returns a list of Subject objects.
     *
     * @param response The XML response string containing course information.
     * @return A list of Subject objects parsed from the XML response.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     * @throws IOException                  If an I/O error occurs while parsing the XML.
     * @throws SAXException                 If any parse errors occur while parsing the XML.
     */
    public static List<Subject> parseCourseResponse(String response) throws ParserConfigurationException, IOException, SAXException {
        Document document = convertStringToDocument(response);
        return extractSubjectsFromDocument(document);
    }

    /**
     * Converts an XML string into a Document object.
     *
     * @param input The XML string to be converted.
     * @return A Document object representing the XML content.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     * @throws IOException                  If an I/O error occurs while parsing the XML.
     * @throws SAXException                 If any parse errors occur while parsing the XML.
     */
    private static Document convertStringToDocument(String input) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(input)));
        doc.getDocumentElement().normalize();
        return doc;
    }

    /**
     * Extracts a list of Subject objects from a Document object.
     *
     * @param doc The Document object containing course information.
     * @return A list of Subject objects extracted from the Document.
     */
    private static List<Subject> extractSubjectsFromDocument(Document doc) {
        List<Subject> subjects = new ArrayList<>();
        NodeList courseNodes = doc.getElementsByTagName("course");

        for (int i = 0; i < courseNodes.getLength(); i++) {
            var courseNode = courseNodes.item(i);
            if (courseNode.getNodeType() == Node.ELEMENT_NODE) {
                Element courseElement = (Element) courseNode;
                Subject subject = new Subject();

                String courseNumber = getElementTextContent(courseElement, "courseNumber");
                String semesterCode = getElementTextContent(courseElement, "semesterCode");
                String courseType = getElementTextContent(courseElement, "courseType");

                // if courseNumber, semesterCode, or courseType is null or empty, the course is invalid, skip it
                if (isNullOrEmpty(courseNumber) || isNullOrEmpty(semesterCode) || isNullOrEmpty(courseType)) {
                    continue;
                }

                //add dot to match current system style Format: 182.182
                courseNumber = courseNumber.substring(0, 3) + "." + courseNumber.substring(3);

                String title = getElementTextContentWithNamespace(courseElement, "title", "ns2:de");
                String url = getElementTextContent(courseElement, "url");
                String description = getElementTextContentWithNamespace(courseElement, "objective", "ns2:de");

                subject.setNumber(courseNumber);
                subject.setSemester(semesterCode);
                subject.setType(courseType);
                subject.setTitle(htmlToPlainText(title));
                subject.setUrl(url);
                subject.setDescription(htmlToPlainText(description));

                subjects.add(subject);
            }
        }
        return subjects;
    }

    /**
     * Retrieves the text content of a specified child element of a parent element.
     *
     * @param parentElement The parent element containing the child element.
     * @param tagName       The tag name of the child element.
     * @return The text content of the child element, or null if not found.
     */
    private static String getElementTextContent(Element parentElement, String tagName) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Retrieves the text content of a specified child element of a parent element,
     * checking first for a namespaced child element within the specified tag.
     *
     * @param parentElement    The parent element containing the child element.
     * @param tagName          The tag name of the child element.
     * @param namespaceTagName The tag name of the namespaced child element to check first.
     * @return The text content of the namespaced child element if present, otherwise the regular child element, or null if not found.
     */
    private static String getElementTextContentWithNamespace(Element parentElement, String tagName, String namespaceTagName) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            NodeList nsNodeList = element.getElementsByTagName(namespaceTagName);
            if (nsNodeList.getLength() > 0) {
                return nsNodeList.item(0).getTextContent();
            } else {
                return element.getTextContent();
            }
        }
        return null;
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param value The string to check.
     * @return true if the string is null or empty, false otherwise.
     */
    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Converts HTML content into plain text format by removing HTML tags and
     * converting certain tags into readable text formats.
     *
     * @param html The HTML content to be converted.
     * @return A plain text representation of the input HTML content.
     *         Returns {@code null} if the input HTML is {@code null}.
     */
    private static String htmlToPlainText(String html) {
        if (html == null) {
            return null;
        }

        // Replace HTML tags with appropriate whitespace and line breaks
        String text = html.replaceAll("<h3>", "\n### ")
            .replaceAll("</h3>", "\n")
            .replaceAll("<p>", "\n")
            .replaceAll("</p>", "\n")
            .replaceAll("<ul>", "\n")
            .replaceAll("</ul>", "\n")
            .replaceAll("<li>", "- ")
            .replaceAll("</li>", "\n")
            .replaceAll("<br>", "\n")
            .replaceAll("<[^>]*>", ""); // Remove any other HTML tags

        // Trim extra whitespace
        text = text.replaceAll("\\s+\\n", "\n").trim();
        return text;
    }
}
