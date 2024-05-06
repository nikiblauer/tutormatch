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
        applicationUserDto.name = "name";
        applicationUserDto.matrNumber = 123L;
        applicationUserDto.email = "email@example.com";
        applicationUserDto.telNr = "1234567890";

        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setPassword(applicationUserDto.password);
        applicationUser.setName(applicationUserDto.name);
        applicationUser.setMatrNumber(applicationUserDto.matrNumber);
        applicationUser.setDetails(new ContactDetails(applicationUserDto.email, applicationUserDto.telNr));

        ApplicationUserDto updatedApplicationUserDto = new ApplicationUserDto();
        updatedApplicationUserDto.password = "newPassword";
        updatedApplicationUserDto.name = "newName";
        updatedApplicationUserDto.matrNumber = 456L;
        updatedApplicationUserDto.email = "newEmail@example.com";
        updatedApplicationUserDto.telNr = "0987654321";

        when(userRepository.findById(id)).thenReturn(Optional.of(applicationUser));
        when(userRepository.save(any(ApplicationUser.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ApplicationUserDto returnedUserDto = customUserDetailService.updateUser(id, updatedApplicationUserDto);

        // Assert
        assertEquals(updatedApplicationUserDto.password, returnedUserDto.password);
        assertEquals(updatedApplicationUserDto.name, returnedUserDto.name);
        assertEquals(updatedApplicationUserDto.matrNumber, returnedUserDto.matrNumber);
        assertEquals(updatedApplicationUserDto.email, returnedUserDto.email);
        assertEquals(updatedApplicationUserDto.telNr, returnedUserDto.telNr);
    }
}
