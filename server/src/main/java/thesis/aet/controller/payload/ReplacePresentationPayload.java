package de.tum.cit.aet.thesis.controller.payload;

import de.tum.cit.aet.thesis.constants.ThesisPresentationType;
import de.tum.cit.aet.thesis.constants.ThesisPresentationVisibility;

import java.time.Instant;

public record ReplacePresentationPayload(
        ThesisPresentationType type,
        ThesisPresentationVisibility visibility,
        String location,
        String streamUrl,
        String language,
        Instant date
) { }
