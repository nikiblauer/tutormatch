package at.ac.tuwien.sepr.groupphase.backend.exception;


public class TissClientHttpException extends TissClientException {
    private final int statusCode;

    public TissClientHttpException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
