package de.tum.cit.aet.thesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.ApplicationReviewer;
import de.tum.cit.aet.thesis.entity.NotificationSetting;
import de.tum.cit.aet.thesis.entity.key.ApplicationReviewerId;
import de.tum.cit.aet.thesis.entity.key.NotificationSettingId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, NotificationSettingId> {

}
