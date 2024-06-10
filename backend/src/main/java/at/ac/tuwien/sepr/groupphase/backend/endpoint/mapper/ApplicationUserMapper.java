package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentBaseInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentSubjectInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StudentSubjectsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentAsAdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateStudentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserBanDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSubjectDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Banned;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserSubject;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper

public interface ApplicationUserMapper {
    default StudentDto applicationUserToDto(ApplicationUser applicationUser) {
        StudentDto studentDto = new StudentDto();

        if (applicationUser != null) {
            studentDto.setPassword(applicationUser.getPassword());
            studentDto.setFirstname(applicationUser.getFirstname());
            studentDto.setLastname(applicationUser.getLastname());
            studentDto.setMatrNumber(applicationUser.getMatrNumber());
            studentDto.setId(applicationUser.getId());
            studentDto.setIsBanned(applicationUser.isBanned());
            if (applicationUser.getDetails() != null) {
                studentDto.setEmail(applicationUser.getDetails().getEmail());
                studentDto.setTelNr(applicationUser.getDetails().getTelNr());
                if (applicationUser.getDetails().getAddress() != null) {
                    studentDto.setStreet(applicationUser.getDetails().getAddress().getStreet());
                    studentDto.setAreaCode(applicationUser.getDetails().getAddress().getAreaCode());
                    studentDto.setCity(applicationUser.getDetails().getAddress().getCity());
                }
            }
        } else {
            return null;
        }

        return studentDto;
    }

    default CreateStudentDto mapUserToCreateApplicationUserDto(ApplicationUser applicationUser) {

        CreateStudentDto createStudentDto = new CreateStudentDto();

        if (applicationUser != null) {
            createStudentDto.setPassword(applicationUser.getPassword());
            createStudentDto.setFirstname(applicationUser.getFirstname());
            createStudentDto.setLastname(applicationUser.getLastname());
            createStudentDto.setMatrNumber(applicationUser.getMatrNumber());
            if (applicationUser.getDetails() != null) {
                createStudentDto.setEmail(applicationUser.getDetails().getEmail());
            }
        }

        return createStudentDto;
    }

    default StudentBaseInfoDto mapApplicationUserToApplicationUserDto(ApplicationUser user) {
        if (user == null || user.getDetails() == null) {
            return null;
        }
        StudentBaseInfoDto studentBaseInfoDto = new StudentBaseInfoDto();

        if (user.getDetails().getAddress() != null) {
            studentBaseInfoDto.setCity(user.getDetails().getAddress().getCity() == null ? "" : user.getDetails().getAddress().getCity());
            studentBaseInfoDto.setStreet(user.getDetails().getAddress().getStreet() == null ? "" : user.getDetails().getAddress().getStreet());
            studentBaseInfoDto.setAreaCode(user.getDetails().getAddress().getAreaCode() == null ? 0 : user.getDetails().getAddress().getAreaCode());
        } else {
            studentBaseInfoDto.setCity("");
            studentBaseInfoDto.setAreaCode(0);
            studentBaseInfoDto.setStreet("");
        }
        studentBaseInfoDto.setEmail(user.getDetails().getEmail());
        if (user.getDetails().getTelNr() != null) {
            studentBaseInfoDto.setTelNr(user.getDetails().getTelNr());
        } else {
            studentBaseInfoDto.setTelNr("");
        }
        studentBaseInfoDto.setFirstname(user.getFirstname());
        studentBaseInfoDto.setLastname(user.getLastname());

        return studentBaseInfoDto;
    }

    default StudentSubjectInfoDto applicationUserToSubjectsDto(ApplicationUser user) {
        if (user == null || user.getDetails() == null) {
            return null;
        }

        StudentSubjectInfoDto applicationUserDetailDto = new StudentSubjectInfoDto();

        applicationUserDetailDto.setFirstname(user.getFirstname());
        applicationUserDetailDto.setLastname(user.getLastname());

        StudentSubjectInfoDto userDetailWithSubjectInfo = new StudentSubjectInfoDto();

        userDetailWithSubjectInfo.setFirstname(user.getFirstname());
        userDetailWithSubjectInfo.setLastname(user.getLastname());
        userDetailWithSubjectInfo.setEmail(user.getDetails().getEmail());
        userDetailWithSubjectInfo.setTelNr(user.getDetails().getTelNr());
        userDetailWithSubjectInfo.setStreet(user.getDetails().getAddress().getStreet());
        userDetailWithSubjectInfo.setAreaCode(user.getDetails().getAddress().getAreaCode());
        userDetailWithSubjectInfo.setCity(user.getDetails().getAddress().getCity());
        userDetailWithSubjectInfo.setMatrNumber(user.getMatrNumber());

        return userDetailWithSubjectInfo;
    }

    default StudentSubjectsDto mapUserAndSubjectsToUserSubjectDto(ApplicationUser user, List<UserSubject> userSubjects) {
        if (user == null || user.getDetails() == null) {
            return null;
        }

        StudentSubjectsDto studentSubjectsDto = new StudentSubjectsDto();

        var address = user.getDetails().getAddress();
        if (address != null) {
            studentSubjectsDto.setCity(address.getCity());
            studentSubjectsDto.setStreet(address.getStreet());
            studentSubjectsDto.setAreaCode(address.getAreaCode());
        } else {
            studentSubjectsDto.setCity("");
            studentSubjectsDto.setStreet("");
            studentSubjectsDto.setAreaCode(0);
        }

        studentSubjectsDto.setEmail(user.getDetails().getEmail());
        studentSubjectsDto.setTelNr(user.getDetails().getTelNr());

        studentSubjectsDto.setFirstname(user.getFirstname());
        studentSubjectsDto.setLastname(user.getLastname());
        studentSubjectsDto.setMatrNumber(user.getMatrNumber());
        studentSubjectsDto.setTelNr(user.getDetails().getTelNr());

        List<UserSubjectDto> subjects = userSubjects.stream().map(this::userSubjectToDto).toList();
        studentSubjectsDto.setSubjects(subjects);
        return studentSubjectsDto;
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

    default UpdateStudentDto toUpdateDto(ApplicationUser applicationUserUpdated) {
        UpdateStudentDto dto = new UpdateStudentDto();
        dto.setId(applicationUserUpdated.getId());
        dto.setFirstname(applicationUserUpdated.getFirstname());
        dto.setLastname(applicationUserUpdated.getLastname());
        dto.setTelNr(applicationUserUpdated.getDetails().getTelNr());
        dto.setCity(applicationUserUpdated.getDetails().getAddress().getCity());
        dto.setStreet(applicationUserUpdated.getDetails().getAddress().getStreet());
        dto.setAreaCode(applicationUserUpdated.getDetails().getAddress().getAreaCode());
        return dto;
    }

    default UpdateStudentAsAdminDto toAdminUpdateDto(ApplicationUser applicationUserUpdated) {
        UpdateStudentAsAdminDto dto = new UpdateStudentAsAdminDto();
        dto.setId(applicationUserUpdated.getId());
        dto.setMatrNumber(applicationUserUpdated.getMatrNumber());
        dto.setFirstname(applicationUserUpdated.getFirstname());
        dto.setLastname(applicationUserUpdated.getLastname());
        dto.setTelNr(applicationUserUpdated.getDetails().getTelNr());
        dto.setCity(applicationUserUpdated.getDetails().getAddress().getCity());
        dto.setStreet(applicationUserUpdated.getDetails().getAddress().getStreet());
        dto.setAreaCode(applicationUserUpdated.getDetails().getAddress().getAreaCode());
        return dto;
    }

    default UserBanDetailsDto mapToUserBanDetailsDto(ApplicationUser user, Banned ban) {
        UserBanDetailsDto dto = new UserBanDetailsDto();

        dto.setBanDate(ban.getBanDate());
        dto.setReason(ban.getReason());
        dto.setStreet(user.getDetails().getAddress().getStreet());
        dto.setCity(user.getDetails().getAddress().getCity());
        dto.setAreaCode(user.getDetails().getAddress().getAreaCode());
        dto.setId(user.getId());
        dto.setEmail(user.getDetails().getEmail());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        return dto;
    }
}
