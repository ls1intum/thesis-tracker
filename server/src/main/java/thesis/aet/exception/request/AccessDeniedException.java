package de.tum.cit.aet.thesis.exception.request;

public class AccessDeniedException extends RuntimeException{

    public AccessDeniedException(String message) {
        super(message);
    }
}
