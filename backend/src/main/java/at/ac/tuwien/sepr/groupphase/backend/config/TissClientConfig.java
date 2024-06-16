package at.ac.tuwien.sepr.groupphase.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for TISS Client configuration properties.
 * Provides a way to configure the base URL used by TissClientImpl.
 */
@Configuration
public class TissClientConfig {

    @Value("${tiss.base-url:https://tiss.tuwien.ac.at/}")
    private String baseUrl;

    public String getBaseUrl() {
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }
}
