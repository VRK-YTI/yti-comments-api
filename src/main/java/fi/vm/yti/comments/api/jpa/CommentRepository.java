package fi.vm.yti.comments.api.jpa;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.entity.Comment;

@Repository
@Transactional
public interface CommentRepository extends PagingAndSortingRepository<Comment, String> {

    Comment findById(final UUID commentId);

    Comment findByCommentThreadIdAndSequenceId(final UUID commentThreadId,
                                               final Integer commentThreadSequenceId);

    Set<Comment> findByCommentThreadCommentRoundIdAndUserIdAndParentCommentIsNull(final UUID commentRoundId,
                                                                                  final UUID userId);

    Comment findByCommentThreadIdAndUserIdAndParentCommentIsNull(final UUID commentRoundId,
                                                                 final UUID userId);

    Set<Comment> findByCommentThreadIdOrderByCreatedAsc(final UUID commentThreadId);

    Set<Comment> findByCommentThreadIdAndParentCommentIsNullOrderByCreatedAsc(final UUID commentThreadId);

    Set<Comment> findAll();

    Set<Comment> findByParentComment(final Comment comment);

    @Query(value = "SELECT COUNT(c) FROM comment AS c WHERE c.modified >= :modifiedAfter", nativeQuery = true)
    long modifiedAfterCount(@Param("modifiedAfter") final Date modifiedAfter);

    @Query(value = "SELECT COUNT(c) FROM comment AS c WHERE c.created >= :createdAfter", nativeQuery = true)
    long createdAfterCount(@Param("createdAfter") final Date createdAfter);

    @Query(value = "SELECT nextval(:sequenceName)", nativeQuery = true)
    Integer getNextSequenceId(@Param("sequenceName") final String sequenceName);
}
