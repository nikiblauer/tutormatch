package at.ac.tuwien.sepr.groupphase.backend.config;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;


import java.lang.invoke.MethodHandles;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * WebSocket security configuration class for handling authorization and subscription checks.
 * Implements {@link ChannelInterceptor} to intercept WebSocket messages and enforce security rules.
 */
@Component
public class WebSocketSecurityConfig implements ChannelInterceptor {
    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Intercepts a message before it is sent to perform security checks.
     *
     * @param message the message to be sent.
     * @param channel the channel through which the message is sent.
     * @return the message to be sent if all checks pass, otherwise throws an exception.
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        LOGGER.debug("Authorization header: {}", accessor.getFirstNativeHeader("Authorization"));

        // Checks that only users who are logged in can connect to websocket
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                try {
                    String username = jwtTokenizer.extractUsernameFromVerificationToken(jwt);
                    LOGGER.debug("User: {} connected to websocket.", username);
                    if (username != null) {
                        accessor.setUser(() -> username);
                        return message;
                    }
                } catch (Exception e) {
                    throw HttpClientErrorException.create(UNAUTHORIZED, "Unauthorized", null, null, null);
                }
            }
            // If token is missing or invalid, reject the connection
            throw HttpClientErrorException.create(UNAUTHORIZED, "No valid JWT token provided", null, null, null);
        }

        // Checks subscriptions to ensure they are authorized
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            if (accessor.getUser() == null) {
                throw HttpClientErrorException.create(UNAUTHORIZED, "Not yet connected!", null, null, null);
            }
            String username = accessor.getUser().getName();

            LOGGER.debug("Subscription destination: {}", destination);
            if (destination != null && username != null) {
                ApplicationUser user = userService.findApplicationUserByEmail(username);
                Long userId = user.getId();

                String expectedPrefix = "/user/" + userId + "/queue/";
                if (!destination.startsWith(expectedPrefix)) {
                    throw HttpClientErrorException.create(UNAUTHORIZED, "Subscription to unauthorized destination", null, null, null);
                }
            } else {
                throw HttpClientErrorException.create(BAD_REQUEST, "Destination or username not provided", null, null, null);
            }
        }

        return message;
    }

}
