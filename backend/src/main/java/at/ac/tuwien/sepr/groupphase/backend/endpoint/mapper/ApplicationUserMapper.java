package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDetailDto;
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
            applicationUserDto.setId(applicationUser.getId());
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

    default ApplicationUserDetailDto mapApplicationUserToApplicationUserDto(ApplicationUser user) {
        if (user == null || user.getDetails() == null) {
            return null;
        }
        ApplicationUserDetailDto applicationUserDetailDto = new ApplicationUserDetailDto();

        if (user.getDetails().getAddress() != null) {
            applicationUserDetailDto.setCity(user.getDetails().getAddress().getCity() == null ? "" : user.getDetails().getAddress().getCity());
            applicationUserDetailDto.setStreet(user.getDetails().getAddress().getStreet() == null ? "" : user.getDetails().getAddress().getStreet());
            applicationUserDetailDto.setAreaCode(user.getDetails().getAddress().getAreaCode() == null ? 0 : user.getDetails().getAddress().getAreaCode());
        } else {
            applicationUserDetailDto.setCity("");
            applicationUserDetailDto.setAreaCode(0);
            applicationUserDetailDto.setStreet("");
        }
        applicationUserDetailDto.setEmail(user.getDetails().getEmail());
        if (user.getDetails().getTelNr() != null) {
            applicationUserDetailDto.setTelNr(user.getDetails().getTelNr());
        } else {
            applicationUserDetailDto.setTelNr("");
        }
        applicationUserDetailDto.setFirstname(user.getFirstname());
        applicationUserDetailDto.setLastname(user.getLastname());

        return applicationUserDetailDto;
    }
}
