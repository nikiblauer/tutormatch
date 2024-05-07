package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
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

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    public void testUpdateUserMethod() throws Exception {
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
        ApplicationUserDto returnedUserDto = customUserDetailService.updateUser(id, updatedApplicationUserDto);

        // Assert
        assertAll(
            () -> assertEquals(updatedApplicationUserDto.password, returnedUserDto.password),
            () -> assertEquals(updatedApplicationUserDto.lastname, returnedUserDto.lastname),
            () -> assertEquals(updatedApplicationUserDto.firstname, returnedUserDto.firstname),
            () -> assertEquals(updatedApplicationUserDto.matrNumber, returnedUserDto.matrNumber),
            () -> assertEquals(updatedApplicationUserDto.email, returnedUserDto.email),
            () -> assertEquals(updatedApplicationUserDto.telNr, returnedUserDto.telNr)
        );
    }

    @Test
    public void testGetAllUsers() {
        // Arrange
        ApplicationUser applicationUser1 = new ApplicationUser();
        applicationUser1.setPassword("password1");
        applicationUser1.setFirstname("Firstname1");
        applicationUser1.setLastname("Lastname1");
        applicationUser1.setMatrNumber(123L);
        applicationUser1.setDetails(new ContactDetails("email1@example.com", "1234567890"));

        ApplicationUser applicationUser2 = new ApplicationUser();
        applicationUser2.setPassword("password2");
        applicationUser2.setFirstname("Firstname2");
        applicationUser2.setLastname("Lastname2");
        applicationUser2.setMatrNumber(456L);
        applicationUser2.setDetails(new ContactDetails("email2@example.com", "0987654321"));

        List<ApplicationUser> applicationUsers = List.of(applicationUser1, applicationUser2);

        when(userRepository.findAll()).thenReturn(applicationUsers);

        // Act
        List<ApplicationUserDto> returnedUserDtos = customUserDetailService.getAllUsers();

        // Assert
        assertEquals(2, returnedUserDtos.size());

        ApplicationUserDto returnedUserDto1 = returnedUserDtos.get(0);
        assertAll(
            () -> assertEquals(applicationUser1.getPassword(), returnedUserDto1.password),
            () -> assertEquals(applicationUser1.getFirstname(), returnedUserDto1.firstname),
            () -> assertEquals(applicationUser1.getLastname(), returnedUserDto1.lastname),
            () -> assertEquals(applicationUser1.getMatrNumber(), returnedUserDto1.matrNumber),
            () -> assertEquals(applicationUser1.getDetails().getEmail(), returnedUserDto1.email),
            () -> assertEquals(applicationUser1.getDetails().getTelNr(), returnedUserDto1.telNr)
        );

        ApplicationUserDto returnedUserDto2 = returnedUserDtos.get(1);
        assertAll(
            () -> assertEquals(applicationUser2.getPassword(), returnedUserDto2.password),
            () -> assertEquals(applicationUser2.getFirstname(), returnedUserDto2.firstname),
            () -> assertEquals(applicationUser2.getLastname(), returnedUserDto2.lastname),
            () -> assertEquals(applicationUser2.getMatrNumber(), returnedUserDto2.matrNumber),
            () -> assertEquals(applicationUser2.getDetails().getEmail(), returnedUserDto2.email),
            () -> assertEquals(applicationUser2.getDetails().getTelNr(), returnedUserDto2.telNr)
        );
    }
}
