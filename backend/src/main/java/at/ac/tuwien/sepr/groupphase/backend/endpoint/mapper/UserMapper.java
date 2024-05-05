package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;

public class UserMapper {

    public UserUpdateDto toDto(ApplicationUser user) {
        return new UserUpdateDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            null // Das Passwort sollte nicht zurückgegeben werden
        );
    }
}
