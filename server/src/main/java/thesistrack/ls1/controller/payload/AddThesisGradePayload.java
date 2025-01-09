package thesismanagement.ls1.controller.payload;

import thesismanagement.ls1.constants.ThesisVisibility;

public record AddThesisGradePayload(
        String finalGrade,
        String finalFeedback,
        ThesisVisibility visibility
) {
}
