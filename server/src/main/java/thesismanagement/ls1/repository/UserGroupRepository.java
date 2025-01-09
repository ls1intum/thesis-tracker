package thesismanagement.ls1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesismanagement.ls1.entity.UserGroup;
import thesismanagement.ls1.entity.key.UserGroupId;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {
    void deleteByUserId(UUID id);
}
