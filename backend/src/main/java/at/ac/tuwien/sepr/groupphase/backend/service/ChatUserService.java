/*
package at.ac.tuwien.sepr.groupphase.backend.service;


import at.ac.tuwien.sepr.groupphase.backend.entity.ChatUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatUserService {

    private final ChatUserRepository userRepository;

    public void saveUser(ChatUser user){
        user.setStatus("ONLINE");
        userRepository.save(user);
    }

    public void disconnect(ChatUser user){
        var storedUser = userRepository.findById(user.getNickName())
            .orElse(null);
        if (storedUser != null){
            storedUser.setStatus("OFFLINE");
            userRepository.save(storedUser);
        }
    }

    public List<ChatUser> findConnectedUsers() {
        return userRepository.findAllByStatus("ONLINE");
    }
}
*/
