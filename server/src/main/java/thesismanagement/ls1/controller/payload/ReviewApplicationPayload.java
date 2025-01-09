package thesismanagement.ls1.controller.payload;

import thesismanagement.ls1.constants.ApplicationReviewReason;

public record ReviewApplicationPayload(
        ApplicationReviewReason reason
) {
}
