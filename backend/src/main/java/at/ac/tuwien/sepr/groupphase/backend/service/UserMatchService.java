package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserMatchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;

import java.util.stream.Stream;

public interface UserMatchService {

    /**
     * Retrieves the matchings of the given user.
     *
     * @param email of the user
     * @return all matchings for this user
     */
    Stream<UserMatchDto> findMatchingsForUser(String email);

}
