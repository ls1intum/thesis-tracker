package thesismanagement.ls1.controller.payload;

import thesismanagement.ls1.constants.ApplicationRejectReason;

public record RejectApplicationPayload (
        ApplicationRejectReason reason,
        Boolean notifyUser
) { }
