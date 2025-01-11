package de.tum.cit.aet.thesis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import de.tum.cit.aet.thesis.constants.ThesisPresentationState;
import de.tum.cit.aet.thesis.constants.ThesisPresentationVisibility;
import de.tum.cit.aet.thesis.entity.ThesisPresentation;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ThesisPresentationRepository extends JpaRepository<ThesisPresentation, UUID> {
    @Query(
            "SELECT p FROM ThesisPresentation p WHERE " +
                    "p.scheduledAt >= :time AND " +
                    "(:states IS NULL OR p.state IN :states) AND " +
                    "(:visibilities IS NULL OR p.visibility IN :visibilities)"
    )
    Page<ThesisPresentation> findFuturePresentations(
            @Param("time") Instant time,
            @Param("states") Set<ThesisPresentationState> states,
            @Param("visibilities") Set<ThesisPresentationVisibility> visibilities,
            Pageable page
    );

    @Query("SELECT p FROM ThesisPresentation p WHERE (:visibilities IS NULL OR p.visibility IN :visibilities)")
    List<ThesisPresentation> findAllPresentations(
            @Param("visibilities") Set<ThesisPresentationVisibility> visibilities
    );
}
