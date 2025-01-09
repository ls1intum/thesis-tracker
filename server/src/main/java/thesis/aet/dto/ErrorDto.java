package de.tum.cit.aet.thesis.dto;

import java.time.Instant;

public record ErrorDto (
    Instant timestamp,
    String message,
    String exception
) {
    public static ErrorDto fromRuntimeException(RuntimeException error) {
        return new ErrorDto(
            Instant.now(),
            error.getMessage(),
            error.getClass().getName()
        );
    }
}
