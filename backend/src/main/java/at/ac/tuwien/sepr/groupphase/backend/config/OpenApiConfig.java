package at.ac.tuwien.sepr.groupphase.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
    info = @Info(
        description = "OpenApi documentation for TutorMatch",
        title = "TutorMatch Documentation",
        version = "v1",
        contact = @Contact(
            email = "tutormatchemail@gmail.com"
        )
    ),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT authentication token",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenApiCustomiser globalResponseCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
        }));
    }

    /*private void removeContentFromApiResponse(ApiResponses responses, String statusCode) {
        if (responses.containsKey(statusCode)) {
            ApiResponse apiResponse = responses.get(statusCode);
            //apiResponse.setContent(new Content()); // Setzen Sie Content explizit auf eine leere Instanz
            apiResponse.setDescription("No content available for this response"); // Optional: Update der Beschreibung
        }
    }*/
}
