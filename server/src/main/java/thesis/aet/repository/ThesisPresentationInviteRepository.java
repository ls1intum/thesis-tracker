package de.tum.cit.aet.thesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.ThesisFeedback;
import de.tum.cit.aet.thesis.entity.ThesisPresentationInvite;
import de.tum.cit.aet.thesis.entity.key.ThesisPresentationInviteId;

import java.util.UUID;


@Repository
public interface ThesisPresentationInviteRepository extends JpaRepository<ThesisPresentationInvite, ThesisPresentationInviteId> {
    void deleteByPresentationId(UUID id);
}
