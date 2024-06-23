package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import org.springframework.beans.factory.annotation.Value;

public interface DataGeneratorConstants {
    String USER_PASSWORD = "Password123";
    String ADMIN_NAME = "Max Mustermann";
    String ADMIN_EMAIL = "test@admin.at";

    @Value("${myapp.user-count}")
    Integer USER_COUNT = 10;
    String SUBJECT_RESOURCE_FILE = "lva_data.csv";
    String BANNED_USER_EMAIL = "e11000001@student.tuwien.ac.at";
}
