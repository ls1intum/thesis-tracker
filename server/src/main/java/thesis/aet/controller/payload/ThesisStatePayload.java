package de.tum.cit.aet.thesis.controller.payload;

import de.tum.cit.aet.thesis.constants.ThesisState;

import java.time.Instant;

public record ThesisStatePayload(
        ThesisState state,
        Instant changedAt
) { }
