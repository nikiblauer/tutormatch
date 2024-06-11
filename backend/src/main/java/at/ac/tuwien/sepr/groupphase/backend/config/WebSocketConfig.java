package at.ac.tuwien.sepr.groupphase.backend.config;


import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/user"); // Enable user-specific message broker
        config.setApplicationDestinationPrefixes("/app"); // this is where all messages are received
        config.setUserDestinationPrefix(("/user"));
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // endpoint for websocket connection
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:4200", "https://*.apps.student.inso-w.at").withSockJS();
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {

        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);

        return false;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        LOGGER.trace("configureClientInboundChannel({})", registration);

        registration.interceptors(new ChannelInterceptor() {
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
                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    if(accessor.getUser() == null){
                        throw HttpClientErrorException.create(UNAUTHORIZED, "Not yet connected!", null, null, null);
                    }
                    String username = accessor.getUser().getName();

                    LOGGER.debug("Subscription destination: {}", destination);
                    if (destination != null && username != null) {
                        ApplicationUser user = userService.findApplicationUserByEmail(username);
                        Long userId = user.getId();

                        String expectedPrefix = "/user/" + userId + "/queue/messages";
                        if (!destination.startsWith(expectedPrefix)) {
                            throw HttpClientErrorException.create(UNAUTHORIZED, "Subscription to unauthorized destination", null, null, null);
                        }
                    }
                }

                return message;
            }
        });
    }

}

