package at.ac.tuwien.sepr.groupphase.backend.unittest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestUtils;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.tiss.SubjectProcessor;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubjectProcessorTest {
    @Test
    public void TestParseCourseResponse_ShouldReturnSingeSubject() throws IOException, ParserConfigurationException, SAXException {
        var response = TestUtils.readFileFromClasspath("courseResponse.xml");
        var subjects = SubjectProcessor.parseCourseResponse(response);
        assertEquals(1, subjects.size());
        var subject = subjects.get(0);
        assertAll(
            () -> assertEquals("Einführung in die Programmierung 2", subject.getTitle()),
            () -> assertEquals("VU", subject.getType()),
            () -> assertEquals("https://tiss.tuwien.ac.at/course/courseDetails.xhtml?courseNr=185A92&semester=2024S", subject.getUrl()),
            () -> assertEquals("2024S", subject.getSemester()),
            () -> assertEquals("185.A92", subject.getNumber()),
            () -> assertNotNull(subject.getDescription())
        );
    }

    @Test
    public void testParseCourseResponse_ShouldReturnMultipleSubjects() throws IOException, ParserConfigurationException, SAXException {
        var response = TestUtils.readFileFromClasspath("orgUnitResponse.xml");
        var subjects = SubjectProcessor.parseCourseResponse(response);

        // Assert the number of subjects
        assertEquals(4, subjects.size());

        // Define the expected values for each subject
        var expectedSubjects = List.of(
            new Subject(null, "Dependable Systems", "VU", "191.109", "2024S", "https://tiss.tuwien.ac.at/course/courseDetails.xhtml?courseNr=191109&semester=2024S", "Description 1"),
            new Subject(null, "Praktikum Technische Informatik", "PR",  "191.005", "2024S","https://tiss.tuwien.ac.at/course/courseDetails.xhtml?courseNr=191005&semester=2024S", "Description 2"),
            new Subject(null, "Elektrotechnische Grundlagen", "LU", "182.692", "2024S", "https://tiss.tuwien.ac.at/course/courseDetails.xhtml?courseNr=182692&semester=2024S", "Description 3"),
            new Subject(null, "Seminar für DissertantInnen", "SE", "182.070", "2024S", "https://tiss.tuwien.ac.at/course/courseDetails.xhtml?courseNr=182070&semester=2024S", "Description 3")
        );

        // Assert each subject's details
        for (int i = 0; i < subjects.size(); i++) {
            var subject = subjects.get(i);
            var expectedSubject = expectedSubjects.get(i);

            assertAll("subject " + (i + 1),
                () -> assertEquals(expectedSubject.getTitle(), subject.getTitle()),
                () -> assertEquals(expectedSubject.getType(), subject.getType()),
                () -> assertEquals(expectedSubject.getUrl(), subject.getUrl()),
                () -> assertEquals(expectedSubject.getSemester(), subject.getSemester()),
                () -> assertEquals(expectedSubject.getNumber(), subject.getNumber()),
                () -> assertNotNull(subject.getDescription())
            );
        }
    }
}
