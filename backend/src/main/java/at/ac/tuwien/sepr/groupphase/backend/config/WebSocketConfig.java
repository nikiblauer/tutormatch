package at.ac.tuwien.sepr.groupphase.backend.config;


import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;


/**
 * Configuration class for setting up WebSocket message broker in the application.
 * It enables WebSocket message handling, configures STOMP endpoints, and sets up message converters and security interceptors.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private UserService userService;

    @Autowired
    private WebSocketSecurityConfig webSocketSecurityConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    /**
     * Configures the message broker.
     * Sets destination for subscription routes
     *
     * @param config the {@link MessageBrokerRegistry} to configure.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/user"); // Enable user-specific message broker
        config.setApplicationDestinationPrefixes("/app"); // this is where all messages are received
        config.setUserDestinationPrefix(("/user"));
    }

    /**
     * Registers STOMP endpoints for WebSocket connections.
     *
     * @param registry the {@link StompEndpointRegistry} to register the endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // endpoint for websocket connection
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:4200", "https://24ss-se-pr-qse-09-0hxytpwzfmwhix6ftcru1.apps.student.inso-w.at").withSockJS();
    }

    /**
     * Configures the message converters to use JSON format.
     *
     * @param messageConverters the list of {@link MessageConverter} to configure.
     * @return false indicating that the default converters should not be overridden.
     */
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

    /**
     * Configures the client inbound channel with security interceptors.
     *
     * @param registration the {@link ChannelRegistration} to configure.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        LOGGER.trace("configureClientInboundChannel({})", registration);
        registration.interceptors(webSocketSecurityConfig);
    }

}

