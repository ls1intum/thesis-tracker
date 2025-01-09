package thesismanagement.ls1.controller.payload;

import thesismanagement.ls1.constants.ThesisCommentType;

public record PostThesisCommentPayload(
        String message,
        ThesisCommentType commentType
) {
}
