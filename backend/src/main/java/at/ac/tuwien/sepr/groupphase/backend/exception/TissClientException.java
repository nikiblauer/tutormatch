package at.ac.tuwien.sepr.groupphase.backend.exception;


public class TissClientException extends Exception  {

    public TissClientException() {
    }

    public TissClientException(String message) {
        super(message);
    }

    public TissClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public TissClientException(Exception e) {
        super(e);
    }
}
