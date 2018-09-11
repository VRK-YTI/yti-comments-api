package fi.vm.yti.comments.api.jpa;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import fi.vm.yti.comments.api.entity.CommentRound;

@Repository
@Transactional
public interface CommentRoundRepository extends PagingAndSortingRepository<CommentRound, String> {

    CommentRound findById(final UUID commentRoundId);

    Set<CommentRound> findAll();
}
