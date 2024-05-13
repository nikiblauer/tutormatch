package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGeneratorConstants.SUBJECT_RESOURCE_FILE;

@Slf4j
@Profile("generateData")
@Component
public class SubjectDataGenerator {

    private final SubjectRepository subjectRepository;
    private final ResourceLoader resourceLoader;

    @Autowired
    public SubjectDataGenerator(SubjectRepository subjectRepository, ResourceLoader resourceLoader) {
        this.subjectRepository = subjectRepository;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void generateSubjects() throws IOException {
        if (subjectRepository.existsById(1L)) {
            log.info("Subject data already generated. Skipping generation.");
            return;
        }

        log.info("Generating subjects...");

        var subjects = readCsvData();
        subjectRepository.saveAll(subjects);
        log.info("Subject data generation completed.");
    }

    private List<Subject> readCsvData() throws IOException {


        List<Subject> subjects = new ArrayList<>();

        Resource resource = resourceLoader.getResource("classpath:" + SUBJECT_RESOURCE_FILE);
        // Check if the resource exists
        if (!resource.exists()) {
            throw new IllegalArgumentException("File not found: " + SUBJECT_RESOURCE_FILE);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                //Format: LVANr, TYP, URL, Name, HOURS, ECTS, SEMESTER
                String[] values = line.split(","); // Adjust delimiter if needed
                Subject subject = new Subject();
                subject.setNumber(values[0]);
                subject.setType(values[1]);
                subject.setUrl(values[2]);
                subject.setTitle(values[3]);
                subject.setDescription("HOURS:" + values[4] + ", ECTS:" + values[5]);
                subject.setSemester(values[6]);
                subjects.add(subject);
            }
        }
        return subjects;
    }
}
