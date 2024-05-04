package at.ac.tuwien.sepr.groupphase.backend.endpoint.exceptionhandler;

import java.util.List;

/**
 * DTO to bundle Validation errors.
 */
public record ValidationErrorRestDto(
    String message,
    List<String> errors
) {
}
