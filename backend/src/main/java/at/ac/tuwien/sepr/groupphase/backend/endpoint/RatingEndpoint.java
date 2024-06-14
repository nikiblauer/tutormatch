package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RatingDto;
import at.ac.tuwien.sepr.groupphase.backend.service.RatingService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/rating")
@Tag(name = "Rating Endpoint")
public class RatingEndpoint {
    private final RatingService ratingService;
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public RatingEndpoint(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
    }

    @Operation(
        description = "Update the rating a user gave another user. 0.5-5 are valid ratings",
        summary = "Update Rating.")
    @Secured("ROLE_USER")
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRating(@RequestBody RatingDto ratingDto) throws Exception {
        LOGGER.info("PUT /api/v1/rating {}", ratingDto);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long ratingUserid = userService.findApplicationUserByEmail(userEmail).getId();
        ratingService.updatedRating(ratingDto, ratingUserid);
    }

    @Operation(
        description = "Get the rating of a user. Rating is a float from 0.5-5",
        summary = "Get rating.")
    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public float getRatingFromUser(@PathVariable("id") Long id) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long ratingUserid = userService.findApplicationUserByEmail(userEmail).getId();
        return ratingService.getRatingFromStudent(id, ratingUserid);
    }

}
