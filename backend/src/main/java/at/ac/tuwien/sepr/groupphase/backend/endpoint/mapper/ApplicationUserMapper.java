package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserBaseInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailsWithSubjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserSubjectsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSubjectDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper

public interface ApplicationUserMapper {
    default ApplicationUserDto applicationUserToDto(ApplicationUser applicationUser) {
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

    default UserBaseInfoDto mapApplicationUserToApplicationUserDto(ApplicationUser user) {
        if (user == null || user.getDetails() == null) {
            return null;
        }
        UserBaseInfoDto userBaseInfoDto = new UserBaseInfoDto();

        if (user.getDetails().getAddress() != null) {
            userBaseInfoDto.setCity(user.getDetails().getAddress().getCity() == null ? "" : user.getDetails().getAddress().getCity());
            userBaseInfoDto.setStreet(user.getDetails().getAddress().getStreet() == null ? "" : user.getDetails().getAddress().getStreet());
            userBaseInfoDto.setAreaCode(user.getDetails().getAddress().getAreaCode() == null ? 0 : user.getDetails().getAddress().getAreaCode());
        } else {
            userBaseInfoDto.setCity("");
            userBaseInfoDto.setAreaCode(0);
            userBaseInfoDto.setStreet("");
        }
        userBaseInfoDto.setEmail(user.getDetails().getEmail());
        if (user.getDetails().getTelNr() != null) {
            userBaseInfoDto.setTelNr(user.getDetails().getTelNr());
        } else {
            userBaseInfoDto.setTelNr("");
        }
        userBaseInfoDto.setFirstname(user.getFirstname());
        userBaseInfoDto.setLastname(user.getLastname());

        return userBaseInfoDto;
    }

    default UserDetailsWithSubjectDto applicationUserToSubjectsDto(ApplicationUser user) {
        if (user == null || user.getDetails() == null) {
            return null;
        }

        UserDetailsWithSubjectDto applicationUserDetailDto = new UserDetailsWithSubjectDto();

        applicationUserDetailDto.setFirstname(user.getFirstname());
        applicationUserDetailDto.setLastname(user.getLastname());

        UserDetailsWithSubjectDto userDetailWithSubjectInfo = new UserDetailsWithSubjectDto();

        userDetailWithSubjectInfo.setFirstname(user.getFirstname());
        userDetailWithSubjectInfo.setLastname(user.getLastname());
        userDetailWithSubjectInfo.setEmail(user.getDetails().getEmail());
        userDetailWithSubjectInfo.setTelNr(user.getDetails().getTelNr());
        userDetailWithSubjectInfo.setStreet(user.getDetails().getAddress().getStreet());
        userDetailWithSubjectInfo.setAreaCode(user.getDetails().getAddress().getAreaCode());
        userDetailWithSubjectInfo.setCity(user.getDetails().getAddress().getCity());

        return userDetailWithSubjectInfo;
    }

    default ApplicationUserSubjectsDto mapUserAndSubjectsToUserSubjectDto(ApplicationUser user, List<UserSubject> userSubjects) {
        if (user == null || user.getDetails() == null) {
            return null;
        }

        ApplicationUserSubjectsDto applicationUserSubjectsDto = new ApplicationUserSubjectsDto();

        var address = user.getDetails().getAddress();
        if (address != null) {
            applicationUserSubjectsDto.setCity(address.getCity());
            applicationUserSubjectsDto.setStreet(address.getStreet());
            applicationUserSubjectsDto.setAreaCode(address.getAreaCode());
        } else {
            applicationUserSubjectsDto.setCity("");
            applicationUserSubjectsDto.setStreet("");
            applicationUserSubjectsDto.setAreaCode(0);
        }

        applicationUserSubjectsDto.setEmail(user.getDetails().getEmail());
        applicationUserSubjectsDto.setTelNr(user.getDetails().getTelNr());

        applicationUserSubjectsDto.setFirstname(user.getFirstname());
        applicationUserSubjectsDto.setLastname(user.getLastname());
        applicationUserSubjectsDto.setMatrNumber(user.getMatrNumber());
        applicationUserSubjectsDto.setTelNr(user.getDetails().getTelNr());

        List<UserSubjectDto> subjects = userSubjects.stream().map(this::userSubjectToDto).toList();
        applicationUserSubjectsDto.setSubjects(subjects);
        return applicationUserSubjectsDto;
    }

    default UserSubjectDto userSubjectToDto(UserSubject userSubject) {
        UserSubjectDto dto = new UserSubjectDto();
        dto.setRole(userSubject.getRole());
        dto.setName(userSubject.getSubject().getType() + " " + userSubject.getSubject().getNumber() + " " + userSubject.getSubject().getTitle());
        dto.setId(userSubject.getSubject().getId());
        dto.setUrl(userSubject.getSubject().getUrl());
        dto.setDescription(userSubject.getSubject().getDescription());
        return dto;
    }

    default UpdateApplicationUserDto toUpdateDto(ApplicationUser applicationUserUpdated) {
        UpdateApplicationUserDto dto = new UpdateApplicationUserDto();
        dto.setId(applicationUserUpdated.getId());
        dto.setFirstname(applicationUserUpdated.getFirstname());
        dto.setLastname(applicationUserUpdated.getLastname());
        dto.setEmail(applicationUserUpdated.getDetails().getEmail());
        dto.setMatrNumber(applicationUserUpdated.getMatrNumber());
        dto.setTelNr(applicationUserUpdated.getDetails().getTelNr());
        dto.setCity(applicationUserUpdated.getDetails().getAddress().getCity());
        dto.setStreet(applicationUserUpdated.getDetails().getAddress().getStreet());
        dto.setAreaCode(applicationUserUpdated.getDetails().getAddress().getAreaCode());
        return dto;
    }
}
