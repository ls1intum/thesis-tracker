package thesismanagement.ls1.dto;

import thesismanagement.ls1.entity.NotificationSetting;

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
