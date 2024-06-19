package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserBlock;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserBlockRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles({"test", "generateData"})
@ExtendWith(MockitoExtension.class)
public class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBlockRepository userBlockRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    void testBlockUser() {
        ApplicationUser user = new ApplicationUser();
        user.setId(1L);

        ApplicationUser userToBlock = new ApplicationUser();
        userToBlock.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userToBlock));

        customUserDetailService.blockUser(1L, 2L);

        verify(userBlockRepository, times(1)).save(any(UserBlock.class));
    }

    @Test
    void testUnblockUser() {
        UserBlock userBlock = new UserBlock();

        when(userBlockRepository.findByUserAndBlockedUser(1L, 2L)).thenReturn(Optional.of(userBlock));

        customUserDetailService.unblockUser(1L, 2L);

        verify(userBlockRepository, times(1)).delete(userBlock);
    }

    @Test
    void testGetBlockedUsers() {
        ApplicationUser user = new ApplicationUser();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userBlockRepository.getBlockedUsers(1L)).thenReturn(Arrays.asList(2L, 3L));

        List<Long> blockedUsers = customUserDetailService.getBlockedUsers(1L);

        assertEquals(2, blockedUsers.size());
        assertTrue(blockedUsers.contains(2L));
        assertTrue(blockedUsers.contains(3L));
    }
}
