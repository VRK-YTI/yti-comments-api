package fi.vm.yti.comments.api.jpa;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;

@Repository
@Transactional
public interface CommentThreadRepository extends PagingAndSortingRepository<CommentThread, String> {

    CommentThread findById(final UUID commentThreadId);

    CommentThread findByCommentRoundIdAndSequenceId(final UUID commentRoundId,
                                                    final Integer commentThreadSequenceId);

    CommentThread findByCommentRoundAndId(final CommentRound commentRound,
                                          final UUID commentThreadId);

    CommentThread findByCommentRoundAndResourceUri(final CommentRound commentRound,
                                                   final String resourceUri);

    Set<CommentThread> findByCommentRoundId(final UUID commentRoundId);

    Page<CommentThread> findByCommentRoundUriInAndUriInAndCreatedBetweenOrCommentRoundUriInAndUriInAndCommentsModifiedBetween(final Set<String> commentRoundUris,
                                                                                                                              final Set<String> uris,
                                                                                                                              final LocalDateTime after,
                                                                                                                              final LocalDateTime before,
                                                                                                                              final Set<String> commentRoundUrisSecondary,
                                                                                                                              final Set<String> urisSecondary,
                                                                                                                              final LocalDateTime commentsModifiedAfter,
                                                                                                                              final LocalDateTime commentsModifiedBefore,
                                                                                                                              final Pageable pageable);

    Page<CommentThread> findByCommentRoundUriInAndUriInAndCreatedAfterOrCommentRoundUriInAndUriInAndCommentsModifiedAfter(final Set<String> commentRoundUris,
                                                                                                                          final Set<String> uris,
                                                                                                                          final LocalDateTime after,
                                                                                                                          final Set<String> commentRoundUrisSecondary,
                                                                                                                          final Set<String> urisSecondary,
                                                                                                                          final LocalDateTime commentsModifiedAfter,
                                                                                                                          final Pageable pageable);

    Page<CommentThread> findByCommentRoundUriInAndUriInAndCreatedBeforeOrCommentRoundUriInAndUriInAndCommentsModifiedAfter(final Set<String> commentRoundUris,
                                                                                                                           final Set<String> uris,
                                                                                                                           final LocalDateTime before,
                                                                                                                           final Set<String> commentRoundUrisSecondary,
                                                                                                                           final Set<String> urisSecondary,
                                                                                                                           final LocalDateTime commentsModifiedBefore,
                                                                                                                           final Pageable pageable);

    Page<CommentThread> findByCommentRoundUriInAndUriIn(final Set<String> commentRoundUris,
                                                        final Set<String> uris,
                                                        final Pageable pageable);

    Page<CommentThread> findByCommentRoundUriInAndCreatedBetweenOrCommentRoundUriInAndCommentsModifiedBetween(final Set<String> commentRoundUris,
                                                                                                              final LocalDateTime after,
                                                                                                              final LocalDateTime before,
                                                                                                              final Set<String> commentRoundUrisSecondary,
                                                                                                              final LocalDateTime commentsModifiedAfter,
                                                                                                              final LocalDateTime commentsModifiedBefore,
                                                                                                              final Pageable pageable);

    Page<CommentThread> findByCommentRoundUriInAndCreatedAfterOrCommentRoundUriInAndCommentsModifiedAfter(final Set<String> commentRoundUris,
                                                                                                          final LocalDateTime after,
                                                                                                          final Set<String> commentRoundUrisSecondary,
                                                                                                          final LocalDateTime commentsModifiedAfter,
                                                                                                          final Pageable pageable);

    Page<CommentThread> findByCommentRoundUriInAndCreatedBeforeOrCommentRoundUriInAndCommentsModifiedBefore(final Set<String> commentRoundUris,
                                                                                                            final LocalDateTime before,
                                                                                                            final Set<String> commentRoundUrisSecondary,
                                                                                                            final LocalDateTime commentsModifiedBefore,
                                                                                                            final Pageable pageable);

    Page<CommentThread> findByCommentRoundUriIn(final Set<String> commentRoundUris,
                                                final Pageable pageable);

    Set<CommentThread> findAll();

    Page<CommentThread> findAll(final Pageable pageable);

    Page<CommentThread> findByUriInAndCreatedBetweenOrUriInAndCommentsModifiedBetween(final Set<String> uris,
                                                                                      final LocalDateTime after,
                                                                                      final LocalDateTime before,
                                                                                      final Set<String> urisSecondary,
                                                                                      final LocalDateTime commentsModifiedAfter,
                                                                                      final LocalDateTime commentsModifiedBefore,
                                                                                      final Pageable pageable);

    Page<CommentThread> findByUriInAndCreatedAfterOrUriInAndCommentsModifiedAfter(final Set<String> uris,
                                                                                  final LocalDateTime after,
                                                                                  final Set<String> urisSecondary,
                                                                                  final LocalDateTime commentsModifiedAfter,
                                                                                  final Pageable pageable);

    Page<CommentThread> findByUriInAndCreatedBeforeOrUriInAndCommentsModifiedBefore(final Set<String> uris,
                                                                                    final LocalDateTime before,
                                                                                    final Set<String> urisSecondary,
                                                                                    final LocalDateTime commentsModifiedBefore,
                                                                                    final Pageable pageable);

    Page<CommentThread> findByCreatedBetweenOrCommentsModifiedBetween(final LocalDateTime after,
                                                                      final LocalDateTime before,
                                                                      final LocalDateTime commentsModifiedAfter,
                                                                      final LocalDateTime commentsModifiedBefore,
                                                                      final Pageable pageable);

    Page<CommentThread> findByCreatedAfterOrCommentsModifiedAfter(final LocalDateTime after,
                                                                  final LocalDateTime commentsModifiedAfter,
                                                                  final Pageable pageable);

    Page<CommentThread> findByCreatedBeforeOrCommentsModifiedBefore(final LocalDateTime before,
                                                                    final LocalDateTime commentsModifiedBefore,
                                                                    final Pageable pageable);

    Page<CommentThread> findByUriIn(final Set<String> uris,
                                    final Pageable pageable);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.created >= :createdAfter")
    long createdAfterCount(@Param("createdAfter") final Date createdAfter);

    @Modifying
    @Query(value = "UPDATE CommentThread AS ct SET ct.commentsModified = :timeStamp WHERE ct.id = :commentThreadId")
    int updateCommentsModified(@Param("commentThreadId") final UUID commentThreadId,
                               @Param("timeStamp") final LocalDateTime timeStamp);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.uri IN (:uris) AND ((ct.commentsModified >= :after AND ct.commentsModified < :before) OR (ct.created >= :after AND ct.created < :before))")
    int getCommentThreadCountWithUrisAndAfterAndBefore(@Param("uris") final Set<String> uris,
                                                       @Param("after") final LocalDateTime after,
                                                       @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.uri IN (:uris) AND (ct.commentsModified < :before OR ct.created >= :after)")
    int getCommentThreadCountWithUrisAndAfter(@Param("uris") final Set<String> uris,
                                              @Param("after") final LocalDateTime after);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.uri IN (:uris) AND (ct.commentsModified < :before OR ct.created < :before)")
    int getCommentThreadCountWithUrisAndBefore(@Param("uris") final Set<String> uris,
                                               @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.commentRound.uri IN (:commentRoundUris) AND ((ct.commentsModified >= :after AND ct.commentsModified < :before) OR (ct.created >= :after AND ct.created < :before))")
    int getCommentThreadCountWithCommentRoundUrisAndAfterAndBefore(@Param("commentRoundUris") final Set<String> commentRoundUris,
                                                                   @Param("after") final LocalDateTime after,
                                                                   @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.commentRound.uri IN (:commentRoundUris) AND (ct.commentsModified >= :after OR ct.created >= :after)")
    int getCommentThreadCountWithCommentRoundIdAndAfter(@Param("commentRoundUris") final Set<String> commentRoundUris,
                                                        @Param("after") final LocalDateTime after);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.commentRound.uri IN (:commentRoundUris) AND (ct.commentsModified < :before OR ct.created < :before)")
    int getCommentThreadCountWithCommentRoundIdAndBefore(@Param("commentRoundUris") final Set<String> commentRoundUris,
                                                         @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.commentRound.uri IN (:commentRoundUris)")
    int getCommentThreadCountWithCommentRoundId(@Param("commentRoundUris") final Set<String> commentRoundUris);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct")
    int getCommentThreadCount();

    @Query(value = "SELECT nextval(:sequenceName)", nativeQuery = true)
    Integer getNextSequenceId(@Param("sequenceName") final String sequenceName);
}
