package de.tum.cit.aet.thesis.controller.payload;

import java.time.Instant;
import java.util.UUID;

public record CreateApplicationPayload (
        UUID topicId,
        String thesisTitle,
        String thesisType,
        Instant desiredStartDate,
        String motivation
) {
}
