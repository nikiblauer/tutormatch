package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import org.mapstruct.Mapper;

@Mapper

public interface ApplicationUserMapper {
    ApplicationUserDto userAndDetailsToApplicationUserDto(ApplicationUser user, ContactDetails details);
}
