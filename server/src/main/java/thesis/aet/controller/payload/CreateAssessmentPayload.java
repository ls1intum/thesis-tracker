package de.tum.cit.aet.thesis.controller.payload;

public record CreateAssessmentPayload(
        String summary,
        String positives,
        String negatives,
        String gradeSuggestion
) {
}
