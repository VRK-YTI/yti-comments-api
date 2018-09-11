package fi.vm.yti.comments.api.jpa;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import fi.vm.yti.comments.api.entity.CommentRoundGroup;

@Repository
@Transactional
public interface CommentRoundGroupRepository extends PagingAndSortingRepository<CommentRoundGroup, String> {

    CommentRoundGroup findById(final UUID commentRoundGroupId);

    Set<CommentRoundGroup> findAll();
}
