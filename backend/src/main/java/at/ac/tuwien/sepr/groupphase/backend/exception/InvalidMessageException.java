package at.ac.tuwien.sepr.groupphase.backend.exception;

import lombok.Getter;

@Getter
public class InvalidMessageException extends Exception {
    private final Long userId;

    public InvalidMessageException(String message, Long userId) {
        super(message);
        this.userId = userId;
    }
}
