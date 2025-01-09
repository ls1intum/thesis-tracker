package de.tum.cit.aet.thesis.controller.payload;

import de.tum.cit.aet.thesis.constants.ApplicationRejectReason;

public record RejectApplicationPayload (
        ApplicationRejectReason reason,
        Boolean notifyUser
) { }
