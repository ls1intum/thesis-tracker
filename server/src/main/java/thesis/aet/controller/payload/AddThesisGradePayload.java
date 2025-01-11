package de.tum.cit.aet.thesis.controller.payload;

import de.tum.cit.aet.thesis.constants.ThesisVisibility;

public record AddThesisGradePayload(
        String finalGrade,
        String finalFeedback,
        ThesisVisibility visibility
) {
}
