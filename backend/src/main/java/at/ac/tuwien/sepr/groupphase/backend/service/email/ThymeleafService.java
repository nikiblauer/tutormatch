package at.ac.tuwien.sepr.groupphase.backend.service.email;

import java.util.Map;

public interface ThymeleafService {
    String createContent(String template, Map<String, Object> variables);
}
