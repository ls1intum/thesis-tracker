package thesismanagement.ls1.controller.payload;

import thesismanagement.ls1.constants.ThesisState;

import java.time.Instant;

public record ThesisStatePayload(
        ThesisState state,
        Instant changedAt
) { }
