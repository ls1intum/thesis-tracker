package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.entity.NotificationSetting;

public record NotificationSettingDto(
        String name,
        String email
) {
    public static NotificationSettingDto fromNotificationSettingEntity(NotificationSetting setting) {
        if (setting == null) {
            return null;
        }

        return new NotificationSettingDto(
                setting.getId().getName(),
                setting.getEmail()
        );
    }
}
