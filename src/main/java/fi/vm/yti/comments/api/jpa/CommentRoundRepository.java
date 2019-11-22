package fi.vm.yti.comments.api.jpa;

import java.time.LocalDate;
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

@Repository
@Transactional
public interface CommentRoundRepository extends PagingAndSortingRepository<CommentRound, String> {

    CommentRound findById(final UUID commentRoundId);

    CommentRound findBySequenceId(final Integer commentRoundSequenceId);

    Set<CommentRound> findAll();

    Set<CommentRound> findByOrganizationsIdAndStatus(final UUID id,
                                                     final String status);

    Set<CommentRound> findByOrganizationsId(final UUID id);

    Set<CommentRound> findByStatus(final String status);

    Set<CommentRound> findByStatusAndEndDateBefore(final String status,
                                                   final LocalDate now);

    Set<CommentRound> findByStatusAndStartDateLessThanEqual(final String status,
                                                            final LocalDate now);

    Set<CommentRound> findBySourceContainerType(final String containerType);

    Set<CommentRound> findByOrganizationsIdAndStatusAndSourceContainerType(final UUID id,
                                                                           final String status,
                                                                           final String containerType);

    Set<CommentRound> findByOrganizationsIdAndSourceContainerType(final UUID id,
                                                                  final String containerType);

    Set<CommentRound> findByStatusAndSourceContainerType(final String status,
                                                         final String containerType);

    Page<CommentRound> findByUriInAndModifiedBetweenOrContentModifiedBetween(final Set<String> uris,
                                                                             final LocalDateTime after,
                                                                             final LocalDateTime before,
                                                                             final LocalDateTime contentModifiedAfter,
                                                                             final LocalDateTime contentModifiedBefore,
                                                                             final Pageable pageable);

    Page<CommentRound> findByUriInAndModifiedAfterOrContentModifiedAfter(final Set<String> uris,
                                                                         final LocalDateTime after,
                                                                         final LocalDateTime contentModifiedAfter,
                                                                         final Pageable pageable);

    Page<CommentRound> findByUriInAndModifiedBeforeOrContentModifiedBefore(final Set<String> uris,
                                                                           final LocalDateTime before,
                                                                           final LocalDateTime contentModifiedBefore,
                                                                           final Pageable pageable);

    Page<CommentRound> findByUriIn(final Set<String> uris,
                                   final Pageable pageable);

    Page<CommentRound> findByModifiedBetweenOrContentModifiedBetween(final LocalDateTime after,
                                                                     final LocalDateTime before,
                                                                     final LocalDateTime contentModifiedAfter,
                                                                     final LocalDateTime contentModifiedBefore,
                                                                     final Pageable pageable);

    Page<CommentRound> findByModifiedAfterOrContentModifiedAfter(final LocalDateTime after,
                                                                 final LocalDateTime contentModifiedAfter,
                                                                 final Pageable pageable);

    Page<CommentRound> findByModifiedBeforeOrContentModifiedBefore(final LocalDateTime before,
                                                                   final LocalDateTime contentModifiedBefore,
                                                                   final Pageable pageable);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.modified >= :modifiedAfter")
    long modifiedAfterCount(@Param("modifiedAfter") final Date modifiedAfter);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.created >= :createdAfter")
    long createdAfterCount(@Param("createdAfter") final Date createdAfter);

    @Modifying
    @Query(value = "UPDATE CommentRound AS cr SET cr.contentModified = :timeStamp WHERE cr.id = :commentRoundId")
    int updateContentModified(@Param("commentRoundId") final UUID commentRoundId,
                              @Param("timeStamp") final LocalDateTime timeStamp);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.uri IN (:commentRoundUris) AND (cr.modified >= :after OR cr.contentModified >=: after) AND (cr.modified < :before OR cr.contentModified < :before)")
    int getCommentRoundCountWithUrisAndAfterAndBefore(@Param("commentRoundUris") final Set<String> commentRoundUris,
                                                      @Param("after") final LocalDateTime after,
                                                      @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE (cr.modified >= :after OR cr.contentModified >= :after) AND (cr.modified < :before OR cr.contentModified < :before)")
    int getCommentRoundCountWithAfterAndBefore(@Param("after") final LocalDateTime after,
                                               @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.uri IN (:commentRoundUris) AND (cr.modified >= :after OR cr.contentModified >= :after)")
    int getCommentRoundCountWithUrisAndAfter(@Param("commentRoundUris") final Set<String> commentRoundUris,
                                             @Param("after") final LocalDateTime after);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.uri IN (:commentRoundUris) AND (cr.modified < :before OR cr.contentModified < :before)")
    int getCommentRoundCountWithUrisAndBefore(@Param("commentRoundUris") final Set<String> commentRoundUris,
                                              @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE (cr.modified >= :after OR cr.contentModified >= :after)")
    int getCommentRoundCountWithAfter(@Param("after") final LocalDateTime after);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE (cr.modified < :before OR cr.contentModified < :before)")
    int getCommentRoundCountWithBefore(@Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr")
    int getCommentThreadCount();

    @Query(value = "SELECT nextval('seq_rounds')", nativeQuery = true)
    Integer getNextSequenceId();
}
