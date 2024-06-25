package at.ac.tuwien.sepr.groupphase.backend.basetest;

import java.util.ArrayList;
import java.util.List;

public interface TestData {

    String BASE_URI = "/api/v1";
    String USER_BASE_URI = BASE_URI + "/user";

    String CHAT_BASE_URI = BASE_URI + "/chat";
    String LOGIN_BASE_URI = BASE_URI + "/authentication";

    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
        }
    };
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

    String DEFAULT_USER_EMAIL = "e10000001@student.tuwien.ac.at";
    String ADMIN_EMAIL = "test@admin.at";

}
