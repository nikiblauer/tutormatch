package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class FallbackController {

    @RequestMapping(value = "/**")  // Map to all requests
    public String redirect() {
        return "forward:/index.html";  // Forward to index.html
    }
}