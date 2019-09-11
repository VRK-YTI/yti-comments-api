package fi.vm.yti.comments.api.jpa;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;

@Repository
@Transactional
public interface CommentThreadRepository extends PagingAndSortingRepository<CommentThread, String> {

    CommentThread findById(final UUID commentThreadId);

    CommentThread findByCommentRoundAndId(final CommentRound commentRound,
                                          final UUID commentThreadId);

    CommentThread findByCommentRoundAndResourceUri(final CommentRound commentRound,
                                                   final String resourceUri);

    Set<CommentThread> findByCommentRoundId(final UUID commentRoundId);

    Set<CommentThread> findAll();

    @Query(value = "SELECT COUNT(ct) FROM commentthread AS ct WHERE ct.created >= :createdAfter", nativeQuery = true)
    long createdAfterCount(@Param("createdAfter") final Date createdAfter);
}
