package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import jakarta.annotation.security.PermitAll;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotFoundController implements ErrorController {

    @RequestMapping("/error")
    @PermitAll
    public String handleError() {
        return "forward:/notFound";
    }
}
