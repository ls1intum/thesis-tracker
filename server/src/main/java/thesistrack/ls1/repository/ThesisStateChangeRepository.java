package thesismanagement.ls1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesismanagement.ls1.entity.ThesisStateChange;
import thesismanagement.ls1.entity.key.ThesisStateChangeId;


@Repository
public interface ThesisStateChangeRepository extends JpaRepository<ThesisStateChange, ThesisStateChangeId> {
}
