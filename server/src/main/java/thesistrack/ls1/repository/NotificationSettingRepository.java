package thesismanagement.ls1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesismanagement.ls1.entity.ApplicationReviewer;
import thesismanagement.ls1.entity.NotificationSetting;
import thesismanagement.ls1.entity.key.ApplicationReviewerId;
import thesismanagement.ls1.entity.key.NotificationSettingId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, NotificationSettingId> {

}
