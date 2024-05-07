package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper

public interface ApplicationUserMapper {
    @Mapping(source = "details.email", target = "email")
    @Mapping(source = "details.telNr", target = "telNr")
    ApplicationUserDto applicationUserAndDetailsToApplicationUserDto(ApplicationUser applicationUser, ContactDetails details);
}
