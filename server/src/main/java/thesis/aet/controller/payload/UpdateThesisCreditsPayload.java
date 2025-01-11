package de.tum.cit.aet.thesis.controller.payload;

import java.util.Map;
import java.util.UUID;

public record UpdateThesisCreditsPayload(
        Map<UUID, Number> credits
) { }
