package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdateApplicationUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Address;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ContactDetails;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;


    // Unit Test for queryUsers method in service layer
    /*
    @Test
    void queryForCreatedUserWithFullnameMatrNr() {
        // Arrange
        ApplicationUser applicationUser1 = new ApplicationUser();
        applicationUser1.setPassword("password1");
        applicationUser1.setFirstname("John");
        applicationUser1.setLastname("Doe");
        applicationUser1.setMatrNumber(123L);
        applicationUser1.setDetails(new ContactDetails("JohnDoe@tuwien.ac.at", "1234567890", new Address("Teststraße 2", 1200, "Wien")));

        List<ApplicationUser> applicationUsers = List.of(applicationUser1);

        Pageable pageable = Pageable.unpaged();
                when(userRepository.findAllByFullnameOrMatrNumber("John Doe", 123L, pageable).getContent()).thenReturn(applicationUsers);

        // Act
        List<ApplicationUser> returnedUserDtos = customUserDetailService.queryUsers("John Doe", 123L, pageable).getContent();

        // Assert
        assertEquals(1, returnedUserDtos.size());

        ApplicationUser returnedUserDto1 = returnedUserDtos.get(0);
        assertAll(
            () -> assertEquals(applicationUser1.getPassword(), returnedUserDto1.getPassword()),
            () -> assertEquals(applicationUser1.getFirstname(), returnedUserDto1.getFirstname()),
            () -> assertEquals(applicationUser1.getLastname(), returnedUserDto1.getLastname()),
            () -> assertEquals(applicationUser1.getMatrNumber(), returnedUserDto1.getMatrNumber()),
            () -> assertEquals(applicationUser1.getDetails().getEmail(), returnedUserDto1.getDetails().getEmail()),
            () -> assertEquals(applicationUser1.getDetails().getTelNr(), returnedUserDto1.getDetails().getTelNr()),
            () -> assertEquals(applicationUser1.getDetails().getAddress().getStreet(), returnedUserDto1.getDetails().getAddress().getStreet()),
            () -> assertEquals(applicationUser1.getDetails().getAddress().getAreaCode(), returnedUserDto1.getDetails().getAddress().getAreaCode()),
            () -> assertEquals(applicationUser1.getDetails().getAddress().getCity(), returnedUserDto1.getDetails().getAddress().getCity())
        );
    }
     */
}
