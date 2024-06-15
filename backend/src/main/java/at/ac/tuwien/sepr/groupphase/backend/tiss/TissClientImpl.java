package at.ac.tuwien.sepr.groupphase.backend.tiss;

import at.ac.tuwien.sepr.groupphase.backend.config.TissClientConfig;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.exception.TissClientException;
import at.ac.tuwien.sepr.groupphase.backend.exception.TissClientHttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TissClientImpl implements TissClient {
    private final String baseUrl;
    private static final Logger logger = LoggerFactory.getLogger(TissClientImpl.class);

    @Autowired
    public TissClientImpl(TissClientConfig config) {
        this.baseUrl = config.getBaseUrl();
    }

    @Override
    public Subject getCourseInfo(String number, String semester) throws TissClientException {
        String endpoint = baseUrl + "/api/course/" + number + "-" + semester;
        logger.info("Fetching course info for number: {} and semester: {}", number, semester);

        try {
            String response = sendGetRequest(endpoint);
            var subjects = SubjectProcessor.parseCourseResponse(response);
            return subjects.getFirst();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            logger.error("Error fetching course info for number: {} and semester: {}", number, semester, e);
            throw new TissClientException("Error fetching course info", e);
        }
    }

    @Override
    public List<Subject> getOrgUnitCourses(String orgUnit) throws TissClientException {
        String endpoint = baseUrl + "/api/course/orgUnit/" + orgUnit;
        logger.info("Fetching courses for org unit: {}", orgUnit);

        try {
            String response = sendGetRequest(endpoint);
            return SubjectProcessor.parseCourseResponse(response);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            logger.error("Error fetching courses for org unit: {}", orgUnit, e);
            throw new TissClientException("Error fetching courses for org unit", e);
        }
    }

    @Override
    public List<String> getOrgUnits() throws TissClientException {
        String endpoint = baseUrl + "/adressbuch/adressbuch/organigramm";
        logger.info("Fetching organization units");
        try {
            String response = sendGetRequest(endpoint);
            return extractOrgUnitsFromResponse(response);
        } catch (IOException e) {
            logger.error("Error fetching organization units", e);
            throw new TissClientException("Error fetching organization units", e);
        }
    }

    private static List<String> extractOrgUnitsFromResponse(String response) {
        logger.debug("Extracting org units from response: {}", response);

        // get all orgUnits from response with regex
        String regex = "E\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);

        //Distinct list of orgUnits
        HashSet<String> orgUnits = new HashSet<>();
        while (matcher.find()) {
            var orgUnit = matcher.group();
            if (!orgUnits.contains(orgUnit)) {
                logger.debug("Found new org unit: {}", orgUnit);
                orgUnits.add(matcher.group());
            }
        }
        logger.debug("Extracted org units: {}", orgUnits);
        return List.copyOf(orgUnits);
    }

    private String sendGetRequest(String endpoint) throws IOException, TissClientHttpException {
        logger.info("Sending GET request to endpoint: {}", endpoint);
        var url = URI.create(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/xml");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            logger.info("Received HTTP response code: {}", responseCode);
            throw new TissClientHttpException("Failed : HTTP error code : " + responseCode, responseCode);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            logger.debug("Received response: {}", response.toString());
            conn.disconnect();
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }
}
