package de.tum.cit.aet.thesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.TopicRole;
import de.tum.cit.aet.thesis.entity.key.TopicRoleId;

import java.util.List;
import java.util.UUID;


@Repository
public interface TopicRoleRepository extends JpaRepository<TopicRole, TopicRoleId> {
    List<TopicRole> deleteByTopicId(UUID topicId);
}
