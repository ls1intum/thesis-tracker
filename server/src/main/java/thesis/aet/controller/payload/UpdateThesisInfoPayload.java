package de.tum.cit.aet.thesis.controller.payload;

import java.util.Map;

public record UpdateThesisInfoPayload(
        String abstractText,
        String infoText,
        String primaryTitle,
        Map<String, String> secondaryTitles
) { }
