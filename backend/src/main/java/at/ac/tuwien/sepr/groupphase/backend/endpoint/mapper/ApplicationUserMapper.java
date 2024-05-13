package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import org.mapstruct.Mapper;

@Mapper

public interface ApplicationUserMapper {
    default ApplicationUserDto mapUserToDto(ApplicationUser applicationUser, ContactDetails details) {
        if (applicationUser == null && details == null) {
            return null;
        }

        ApplicationUserDto applicationUserDto = new ApplicationUserDto();

        if (applicationUser != null) {
            applicationUserDto.setPassword(applicationUser.getPassword());
            applicationUserDto.setFirstname(applicationUser.getFirstname());
            applicationUserDto.setLastname(applicationUser.getLastname());
            applicationUserDto.setMatrNumber(applicationUser.getMatrNumber());
        }
        if (details != null) {
            applicationUserDto.setEmail(details.getEmail());
            applicationUserDto.setTelNr(details.getTelNr());
        }

        return applicationUserDto;
    }

    default CreateApplicationUserDto mapUserToCreateApplicationUserDto(ApplicationUser applicationUser, ContactDetails details) {
        if (applicationUser == null && details == null) {
            return null;
        }

        CreateApplicationUserDto createApplicationUserDto = new CreateApplicationUserDto();

        if (applicationUser != null) {
            createApplicationUserDto.setPassword(applicationUser.getPassword());
            createApplicationUserDto.setFirstname(applicationUser.getFirstname());
            createApplicationUserDto.setLastname(applicationUser.getLastname());
            createApplicationUserDto.setMatrNumber(applicationUser.getMatrNumber());
        }
        if (details != null) {
            createApplicationUserDto.setEmail(details.getEmail());
        }

        return createApplicationUserDto;
    }



    //CreateApplicationUserDto mapUserToCreateApplicationUserDto(ApplicationUser a, ContactDetails c);
}
