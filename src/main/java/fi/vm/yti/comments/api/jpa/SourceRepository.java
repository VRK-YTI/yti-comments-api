package fi.vm.yti.comments.api.jpa;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.entity.Source;

@Repository
@Transactional
public interface SourceRepository extends PagingAndSortingRepository<Source, String> {

    Source findById(final UUID sourceId);

    Source findByContainerUri(final String containerUri);

    Set<Source> findAll();
}
