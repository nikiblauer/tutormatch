package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ApplicationUserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator validator;

    @Mock
    private ApplicationUserMapper mapper;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    // Unit Test for updateUser method in service layer
    @Test
    void testUpdateUserMethod() throws Exception {
        // Arrange
        Long id = 1L;
        ApplicationUserDto applicationUserDto = new ApplicationUserDto();
        applicationUserDto.password = "password";
        applicationUserDto.firstname = "Firstname";
        applicationUserDto.lastname = "Lastname";
        applicationUserDto.matrNumber = 123L;
        applicationUserDto.email = "email@example.com";
        applicationUserDto.telNr = "1234567890";

        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setPassword(applicationUserDto.password);
        applicationUser.setFirstname(applicationUserDto.lastname);
        applicationUser.setLastname(applicationUserDto.lastname);
        applicationUser.setMatrNumber(applicationUserDto.matrNumber);
        applicationUser.setDetails(new ContactDetails(applicationUserDto.email, applicationUserDto.telNr));

        ApplicationUserDto updatedApplicationUserDto = new ApplicationUserDto();
        updatedApplicationUserDto.password = "newPassword";
        updatedApplicationUserDto.firstname = "newFirstName";
        updatedApplicationUserDto.lastname = "newLastName";
        updatedApplicationUserDto.matrNumber = 456L;
        updatedApplicationUserDto.email = "newEmail@example.com";
        updatedApplicationUserDto.telNr = "0987654321";

        when(userRepository.findById(id)).thenReturn(Optional.of(applicationUser));
        when(userRepository.save(any(ApplicationUser.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ApplicationUser returnedUserDto = customUserDetailService.updateUser(id, updatedApplicationUserDto);

        // Assert
        assertAll(
            () -> assertEquals(updatedApplicationUserDto.password, returnedUserDto.getPassword()),
            () -> assertEquals(updatedApplicationUserDto.lastname, returnedUserDto.getLastname()),
            () -> assertEquals(updatedApplicationUserDto.firstname, returnedUserDto.getFirstname()),
            () -> assertEquals(updatedApplicationUserDto.matrNumber, returnedUserDto.getMatrNumber()),
            () -> assertEquals(updatedApplicationUserDto.email, returnedUserDto.getDetails().getEmail()),
            () -> assertEquals(updatedApplicationUserDto.telNr, returnedUserDto.getDetails().getTelNr())
        );
    }

    // Unit Test for queryUsers method in service layer
    @Test
    void queryForCreatedUser() {
        // Arrange
        ApplicationUser applicationUser1 = new ApplicationUser();
        applicationUser1.setPassword("password1");
        applicationUser1.setFirstname("John");
        applicationUser1.setLastname("Doe");
        applicationUser1.setMatrNumber(123L);
        applicationUser1.setDetails(new ContactDetails("email1@example.com", "1234567890"));

        ApplicationUser applicationUser2 = new ApplicationUser();
        applicationUser2.setPassword("password2");
        applicationUser2.setFirstname("Firstname2");
        applicationUser2.setLastname("Lastname2");
        applicationUser2.setMatrNumber(456L);
        applicationUser2.setDetails(new ContactDetails("email2@example.com", "0987654321"));

        List<ApplicationUser> applicationUsers = List.of(applicationUser1, applicationUser2);

        ApplicationUserDto applicationUserDto1 = new ApplicationUserDto();

        applicationUserDto1.password = applicationUser1.getPassword();
        applicationUserDto1.firstname = applicationUser1.getFirstname();
        applicationUserDto1.lastname = applicationUser1.getLastname();
        applicationUserDto1.matrNumber = applicationUser1.getMatrNumber();
        applicationUserDto1.email = applicationUser1.getDetails().getEmail();
        applicationUserDto1.telNr = applicationUser1.getDetails().getTelNr();

        when(userRepository.findAll()).thenReturn(applicationUsers);

        // Act
        List<ApplicationUser> returnedUserDtos = customUserDetailService.queryUsers("John", "Doe");

        // Assert
        assertEquals(1, returnedUserDtos.size());

        ApplicationUser returnedUserDto1 = returnedUserDtos.get(0);
        assertAll(
            () -> assertEquals(applicationUserDto1.password, returnedUserDto1.getPassword()),
            () -> assertEquals(applicationUserDto1.firstname, returnedUserDto1.getFirstname()),
            () -> assertEquals(applicationUserDto1.lastname, returnedUserDto1.getLastname()),
            () -> assertEquals(applicationUserDto1.matrNumber, returnedUserDto1.getMatrNumber()),
            () -> assertEquals(applicationUserDto1.email, returnedUserDto1.getDetails().getEmail()),
            () -> assertEquals(applicationUserDto1.telNr, returnedUserDto1.getDetails().getTelNr())
        );
    }
}
