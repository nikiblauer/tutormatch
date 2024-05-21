package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserMatchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;

import java.util.stream.Stream;

public interface UserMatchService {
    //Stream<UserMatchDto> findMatchingUserByUserIdAsStream(long userId);
    Stream<UserMatchDto> findMatchingsForUser(String email);

}
