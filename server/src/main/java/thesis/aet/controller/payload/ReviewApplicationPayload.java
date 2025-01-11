package de.tum.cit.aet.thesis.controller.payload;

import de.tum.cit.aet.thesis.constants.ApplicationReviewReason;

public record ReviewApplicationPayload(
        ApplicationReviewReason reason
) {
}
