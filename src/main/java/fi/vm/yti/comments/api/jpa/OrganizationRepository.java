package fi.vm.yti.comments.api.jpa;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.entity.Organization;

@Repository
@Transactional
public interface OrganizationRepository extends CrudRepository<Organization, String> {

    Organization findById(final UUID id);

    Set<Organization> findByRemovedIsFalse();

    Set<Organization> findByRemovedIsFalseAndCommentRoundsIsNotNull();

    Set<Organization> findAll();
}
