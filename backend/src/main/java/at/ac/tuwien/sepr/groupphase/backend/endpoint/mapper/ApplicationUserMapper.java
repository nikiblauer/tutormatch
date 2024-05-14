package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import org.mapstruct.Mapper;

@Mapper

public interface ApplicationUserMapper {
    default ApplicationUserDto mapUserToDto(ApplicationUser applicationUser) {
        ApplicationUserDto applicationUserDto = new ApplicationUserDto();

        if (applicationUser != null) {
            applicationUserDto.setPassword(applicationUser.getPassword());
            applicationUserDto.setFirstname(applicationUser.getFirstname());
            applicationUserDto.setLastname(applicationUser.getLastname());
            applicationUserDto.setMatrNumber(applicationUser.getMatrNumber());
            if (applicationUser.getDetails() != null) {
                applicationUserDto.setEmail(applicationUser.getDetails().getEmail());
                applicationUserDto.setTelNr(applicationUser.getDetails().getTelNr());
                if (applicationUser.getDetails().getAddress() != null) {
                    applicationUserDto.setStreet(applicationUser.getDetails().getAddress().getStreet());
                    applicationUserDto.setAreaCode(applicationUser.getDetails().getAddress().getAreaCode());
                    applicationUserDto.setCity(applicationUser.getDetails().getAddress().getCity());
                }
            }
        } else {
            return null;
        }

        return applicationUserDto;
    }

    default CreateApplicationUserDto mapUserToCreateApplicationUserDto(ApplicationUser applicationUser) {

        CreateApplicationUserDto createApplicationUserDto = new CreateApplicationUserDto();

        if (applicationUser != null) {
            createApplicationUserDto.setPassword(applicationUser.getPassword());
            createApplicationUserDto.setFirstname(applicationUser.getFirstname());
            createApplicationUserDto.setLastname(applicationUser.getLastname());
            createApplicationUserDto.setMatrNumber(applicationUser.getMatrNumber());
            if (applicationUser.getDetails() != null) {
                createApplicationUserDto.setEmail(applicationUser.getDetails().getEmail());
            }
        }

        return createApplicationUserDto;
    }
}
