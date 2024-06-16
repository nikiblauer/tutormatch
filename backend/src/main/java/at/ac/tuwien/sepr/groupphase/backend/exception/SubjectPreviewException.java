package at.ac.tuwien.sepr.groupphase.backend.exception;


public class SubjectPreviewException extends RuntimeException {

    public SubjectPreviewException() {
    }

    public SubjectPreviewException(String message) {
        super(message);
    }

    public SubjectPreviewException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubjectPreviewException(Exception e) {
        super(e);
    }
}
