package de.tum.cit.aet.thesis.controller.payload;

import jakarta.mail.internet.InternetAddress;

import java.util.List;

public record SchedulePresentationPayload(
        List<InternetAddress> optionalAttendees,
        Boolean inviteChairMembers,
        Boolean inviteThesisStudents
) { }
