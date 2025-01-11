package de.tum.cit.aet.thesis.controller.payload;

import de.tum.cit.aet.thesis.constants.ThesisVisibility;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UpdateThesisPayload(
        String thesisTitle,
        String thesisType,
        String language,
        ThesisVisibility visibility,
        Set<String> keywords,
        Instant startDate,
        Instant endDate,
        List<UUID> studentIds,
        List<UUID> advisorIds,
        List<UUID> supervisorIds,
        List<ThesisStatePayload> states
) {

}
