package de.tum.cit.aet.thesis.exception;

public class CalendarException extends RuntimeException {
    public CalendarException(String message) {
        super(message);
    }

    public CalendarException(String message, Throwable cause) { super(message, cause); }
}
