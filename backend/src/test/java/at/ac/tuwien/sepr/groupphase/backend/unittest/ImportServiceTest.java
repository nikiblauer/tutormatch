package at.ac.tuwien.sepr.groupphase.backend.unittest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.entity.ImportStatus;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.exception.TissClientException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ImportService;
import at.ac.tuwien.sepr.groupphase.backend.tiss.TissClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "generateData"})
@AutoConfigureMockMvc
public class ImportServiceTest extends BaseTest {

    @Autowired
    private SubjectRepository subjectRepository;

    @MockBean
    private TissClient tissClientMock;

    @Autowired
    private ImportService importService;

    private Subject mockedSubject;
    private String importId;

    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
        List<String> orgUnits = new ArrayList<>();
        orgUnits.add("1");
        orgUnits.add("2");
        orgUnits.add("3");
        try {
            when(tissClientMock.getOrgUnits()).thenReturn(orgUnits);
        } catch (Exception e) {
            assertTrue(false, "could not mock orgUnits");
        }

        mockedSubject = new Subject(); // Create a mock Subject object
        mockedSubject.setType("VU");
        mockedSubject.setUrl("https://tiss.tuwien.ac.at/course/courseDetails.xhtml?courseNr=185A92&semester=2024S");
        mockedSubject.setTitle("Programmierung 2");
        mockedSubject.setNumber("xxx.xxx");
        mockedSubject.setSemester("2024S");
        mockedSubject.setDescription("description");
        importId = "importId";

        Date sqlDate = Date.valueOf("2024-01-01");
        importStatusRepository.save(new ImportStatus(null, "ImportId0", ImportStatus.Status.RUNNING, sqlDate));
        sqlDate = Date.valueOf("2024-01-02");
        importStatusRepository.save(new ImportStatus(null, "ImportId1", ImportStatus.Status.COMPLETED, sqlDate));
    }

    @Test
    void cancelImport_ShouldRerollDatabase() throws TissClientException {
        List<Subject> mockSubjects = new ArrayList<>();
        mockSubjects.add(mockedSubject);
        when(tissClientMock.getOrgUnitCourses(anyString()))
            .thenReturn(mockSubjects)
            .thenAnswer(invocation -> {
                //cancel Import
                importService.cancelImport(importId);
                // Mock response for the second call after latch is released
                return Arrays.asList(mockedSubject);
            });
        assertThrows(InterruptedException.class, () -> importService.startImport(importId));
        assertEquals(200, subjectRepository.count());
    }

    @Test
    void startImport_ShouldImportSingleSubject() throws TissClientException, InterruptedException, ValidationException {
        List<Subject> mockSubjects = new ArrayList<>();
        mockSubjects.add(mockedSubject);
        when(tissClientMock.getOrgUnitCourses(anyString()))
            .thenReturn(mockSubjects);
        importService.startImport("importId3");
        assertEquals(201, subjectRepository.count());
    }

    @Test
    void getLastImportStatus_ShouldReturnLastStatus() {
        var sqlDate = Date.valueOf("2024-01-04");
        var expectedStatus = new ImportStatus(null, importId, ImportStatus.Status.COMPLETED, sqlDate);
        importStatusRepository.save(expectedStatus);

        var status = importService.getLastImportStatus();
        assertAll(
            () -> assertEquals(expectedStatus.getImportId(), status.getImportId()),
            () -> assertEquals(expectedStatus.getImportDate(), status.getImportDate()),
            () -> assertEquals(expectedStatus.getStatus().toString(), status.getStatus())
        );
    }

    @Test
    void startImport_ShouldFailWhenImportAlreadyRunning() throws TissClientException {
        List<Subject> mockSubjects = new ArrayList<>();
        mockSubjects.add(mockedSubject);
        when(tissClientMock.getOrgUnitCourses(anyString()))
            .thenReturn(mockSubjects);
        var sqlDate = Date.valueOf("2024-01-05");
        importStatusRepository.save(new ImportStatus(null, "importId2", ImportStatus.Status.RUNNING, sqlDate));
        assertThrows(ValidationException.class, () -> importService.startImport(importId));
        assertEquals(200, subjectRepository.count());
    }
}
