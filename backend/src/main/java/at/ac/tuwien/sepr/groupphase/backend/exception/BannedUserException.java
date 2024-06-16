package at.ac.tuwien.sepr.groupphase.backend.exception;


public class BannedUserException extends RuntimeException {

    public BannedUserException() {
    }

    public BannedUserException(String message) {
        super(message);
    }

    public BannedUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public BannedUserException(Exception e) {
        super(e);
    }
}
