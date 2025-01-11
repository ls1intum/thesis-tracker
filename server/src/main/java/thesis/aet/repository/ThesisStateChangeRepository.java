package de.tum.cit.aet.thesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.ThesisStateChange;
import de.tum.cit.aet.thesis.entity.key.ThesisStateChangeId;


@Repository
public interface ThesisStateChangeRepository extends JpaRepository<ThesisStateChange, ThesisStateChangeId> {
}
