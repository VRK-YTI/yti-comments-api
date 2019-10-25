package fi.vm.yti.comments.api.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

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

    Set<CommentRound> findByIdIn(final Set<UUID> uuids);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.modified >= :modifiedAfter")
    long modifiedAfterCount(@Param("modifiedAfter") final Date modifiedAfter);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.created >= :createdAfter")
    long createdAfterCount(@Param("createdAfter") final Date createdAfter);

    @Modifying
    @Query(value = "UPDATE CommentRound AS cr SET cr.modified = :timeStamp, cr.contentModified = :timeStamp WHERE cr.id = :commentRoundId")
    int updateModifiedAndContentModified(@Param("commentRoundId") final UUID commentRoundId,
                                         @Param("timeStamp") final LocalDateTime timeStamp);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.id IN (:commentRoundIds) AND cr.modified >= :after AND cr.modified < :before")
    int getCommentRoundCountWithIdsAndAfterAndBefore(@Param("commentRoundIds") final Set<UUID> commentRoundIds,
                                                     @Param("after") final LocalDateTime after,
                                                     @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.modified >= :after AND cr.modified < :before")
    int getCommentRoundCountWithAfterAndBefore(@Param("after") final LocalDateTime after,
                                               @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.id IN (:commentRoundIds) AND cr.modified >= :after")
    int getCommentRoundCountWithIdsAndAfter(@Param("commentRoundIds") final Set<UUID> commentRoundIds,
                                            @Param("after") final LocalDateTime after);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.id IN (:commentRoundIds) AND cr.modified < :before")
    int getCommentRoundCountWithIdsAndBefore(@Param("commentRoundIds") final Set<UUID> commentRoundIds,
                                             @Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.modified >= :after")
    int getCommentRoundCountWithAfter(@Param("after") final LocalDateTime after);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr WHERE cr.modified < :before")
    int getCommentRoundCountWithBefore(@Param("before") final LocalDateTime before);

    @Query(value = "SELECT COUNT(cr) FROM CommentRound AS cr")
    int getCommentThreadCount();
}
