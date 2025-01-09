package thesismanagement.ls1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesismanagement.ls1.entity.ThesisProposal;

import java.util.UUID;

@Repository
public interface ThesisProposalRepository extends JpaRepository<ThesisProposal, UUID> {

}
