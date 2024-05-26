package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.entity.ChatUser;
//import at.ac.tuwien.sepr.groupphase.backend.service.ChatUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ChatUserController {

    //private final ChatUserService service;

    public ChatUserController(/*ChatUserService service*/) {
        //this.service = service;
    }

    @MessageMapping("/chatUser.addUser")
    @SendTo("/chatUser/topic")
    public ChatUser addUser(@Payload ChatUser user){
        //service.saveUser(user);
        return user;
    }

    @MessageMapping("/chatUser.disconnectUser")
    @SendTo("/chatUser/topic")
    public ChatUser disconnect(@Payload ChatUser user){
        //service.disconnect(user);
        return user;
    }

    @GetMapping("/chatUsers")
    public ResponseEntity<List<ChatUser>> findConnectedUsers() {
        return null;//return ResponseEntity.ok(service.findConnectedUsers());
    }
}
