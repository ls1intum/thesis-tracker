package de.tum.cit.aet.thesis.controller.payload;

import de.tum.cit.aet.thesis.constants.ThesisCommentType;

public record PostThesisCommentPayload(
        String message,
        ThesisCommentType commentType
) {
}
