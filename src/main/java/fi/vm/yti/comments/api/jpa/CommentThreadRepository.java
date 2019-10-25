package fi.vm.yti.comments.api.jpa;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
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

    Page<CommentThread> findAll(final Pageable pageable);

    Set<CommentThread> findByIdIn(final Set<UUID> uuids);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.created >= :createdAfter")
    long createdAfterCount(@Param("createdAfter") final Date createdAfter);

    @Modifying
    @Query(value = "UPDATE CommentThread AS ct SET ct.commentsModified = :timeStamp WHERE ct.id = :commentThreadId")
    int updateCommentsModified(@Param("commentThreadId") final UUID commentThreadId,
                               @Param("timeStamp") final LocalDateTime timeStamp);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.id IN (:ids) AND ((ct.commentsModified >= :after AND ct.commentsModified < :before) OR (ct.created >= :after AND ct.created < :before))")
    int getCommentThreadCountWithIdsAndAfterAndBefore(@Param("ids") final Set<UUID> ids,
                                                      @Param("after") final LocalDateTime after,
                                                      @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.id IN (:ids) AND (ct.commentsModified < :before OR ct.created >= :after)")
    int getCommentThreadCountWithIdsAndAfter(@Param("ids") final Set<UUID> ids,
                                             @Param("after") final LocalDateTime after);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.id IN :ids AND (ct.commentsModified < :before OR ct.created < :before)")
    int getCommentThreadCountWithIdsAndBefore(@Param("ids") final Set<UUID> ids,
                                              @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.id = :commentRoundId AND ((ct.commentsModified >= :after AND ct.commentsModified < :before) OR (ct.created >= :after AND ct.created < :before))")
    int getCommentThreadCountWithCommentRoundIdAndAfterAndBefore(@Param("commentRoundId") final UUID commentRoundId,
                                                                 @Param("after") final LocalDateTime after,
                                                                 @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.id = :commentRoundId AND (ct.commentsModified >= :after OR ct.created >= :after)")
    int getCommentThreadCountWithCommentRoundIdAndAfter(@Param("commentRoundId") final UUID commentRoundId,
                                                        @Param("after") final LocalDateTime after);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.id =:commentRoundId AND (ct.commentsModified < :before OR ct.created < :before)")
    int getCommentThreadCountWithCommentRoundIdAndBefore(@Param("commentRoundId") final UUID commentRoundId,
                                                         @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct WHERE ct.id = :commentRoundId")
    int getCommentThreadCountWithCommentRoundId(@Param("commentRoundId") final UUID commentRoundId);

    @Query(value = "SELECT COUNT(ct) FROM CommentThread AS ct")
    int getCommentThreadCount();
}
