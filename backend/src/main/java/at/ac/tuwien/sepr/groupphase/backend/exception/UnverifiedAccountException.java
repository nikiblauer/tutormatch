package at.ac.tuwien.sepr.groupphase.backend.exception;

public class UnverifiedAccountException extends RuntimeException {
    public UnverifiedAccountException(String message) {
        super(message);
    }
}
