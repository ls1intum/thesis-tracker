package de.tum.cit.aet.thesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.entity.ThesisAssessment;

import java.util.UUID;

@Repository
public interface ThesisAssessmentRepository extends JpaRepository<ThesisAssessment, UUID> {

}
