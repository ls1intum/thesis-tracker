package de.tum.cit.aet.thesis.exception;

public class UploadException extends RuntimeException {
    public UploadException(String message) {
        super(message);
    }

    public UploadException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
