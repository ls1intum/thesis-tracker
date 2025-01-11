package de.tum.cit.aet.thesis.controller.payload;

import de.tum.cit.aet.thesis.constants.ThesisFeedbackType;

import java.util.List;

public record RequestChangesPayload(
        ThesisFeedbackType type,
        List<RequestedChange> requestedChanges
) {
    public record RequestedChange(
            String feedback,
            Boolean completed
    ) {}
}
