package thesismanagement.ls1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesismanagement.ls1.entity.ThesisFeedback;
import thesismanagement.ls1.entity.ThesisPresentationInvite;
import thesismanagement.ls1.entity.key.ThesisPresentationInviteId;

import java.util.UUID;


@Repository
public interface ThesisPresentationInviteRepository extends JpaRepository<ThesisPresentationInvite, ThesisPresentationInviteId> {
    void deleteByPresentationId(UUID id);
}
