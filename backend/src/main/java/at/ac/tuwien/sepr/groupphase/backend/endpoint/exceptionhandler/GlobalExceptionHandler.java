package at.ac.tuwien.sepr.groupphase.backend.endpoint.exceptionhandler;

import at.ac.tuwien.sepr.groupphase.backend.exception.BannedUserException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ErrorListException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.SubjectPreviewException;
import at.ac.tuwien.sepr.groupphase.backend.exception.UnverifiedAccountException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.invoke.MethodHandles;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Register all your Java exceptions here to map them into meaningful HTTP exceptions
 * If you have special cases which are only important for specific endpoints, use ResponseStatusExceptions
 * https://www.baeldung.com/exception-handling-for-rest-with-spring#responsestatusexception
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Use the @ExceptionHandler annotation to write handler for custom exceptions.
     */
    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Override methods from ResponseEntityExceptionHandler to send a customized HTTP response for a know exception
     * from e.g. Spring
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        //Get all errors
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> {
                String fieldName = err.getField();
                String errorMessage = err.getDefaultMessage();
                if (errorMessage != null && errorMessage.toLowerCase().startsWith(fieldName.toLowerCase() + " ")) {
                    return err.getDefaultMessage();
                }

                return fieldName + " " + errorMessage;
            })
            .collect(Collectors.toList());
        body.put("Validation errors", errors);

        return new ResponseEntity<>(body.toString(), headers, status);

    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)
    protected ResponseEntity<Object> handleValidation(ErrorListException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    protected ResponseEntity<Object> handleBadCredentials(RuntimeException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {UnverifiedAccountException.class})
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    protected ResponseEntity<Object> handleUnverifiedAccount(RuntimeException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = {BannedUserException.class})
    protected ResponseEntity<Object> handleBannedUser(RuntimeException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    //get all unhandled runtime exceptions to have a consistent error response
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {SubjectPreviewException.class})
    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR", content = @Content)
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}