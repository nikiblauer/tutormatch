package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/userMatch")
@Tag(name = "User-Match Endpoint")
public class UserMatchEnpoint {

}
