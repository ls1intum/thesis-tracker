package de.tum.cit.aet.thesis.controller.payload;

public record UpdateNotificationSettingPayload (
        String name,
        String email
) { }
