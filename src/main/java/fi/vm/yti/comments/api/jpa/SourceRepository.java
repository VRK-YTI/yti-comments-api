package fi.vm.yti.comments.api.jpa;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import fi.vm.yti.comments.api.entity.Source;

@Repository
@Transactional
public interface SourceRepository extends PagingAndSortingRepository<Source, String> {

    Source findById(final UUID sourceId);

    Set<Source> findAll();
}
