package de.tum.cit.aet.thesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.ThesisRole;
import de.tum.cit.aet.thesis.entity.key.ThesisRoleId;

import java.util.List;
import java.util.UUID;


@Repository
public interface ThesisRoleRepository extends JpaRepository<ThesisRole, ThesisRoleId> {
    List<ThesisRole> deleteByThesisId(UUID thesisId);
}
