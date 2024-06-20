package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Controller
public class FallbackController {
    @RequestMapping(value = "/**/{path:[^\\.]*}", method = RequestMethod.GET)
    @ResponseBody
    public String redirect(HttpServletRequest request) throws IOException {
        Resource resource = new ClassPathResource("/frontend/dist/sepr-group-phase/browser/index.html");
        Path path = Paths.get(resource.getURI());
        String content = new String(Files.readAllBytes(path), "UTF-8");
        return content;
    }
}
