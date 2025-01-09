package thesismanagement.ls1.controller.payload;

import thesismanagement.ls1.constants.ThesisFeedbackType;

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
