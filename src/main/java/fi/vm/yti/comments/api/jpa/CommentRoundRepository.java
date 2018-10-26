package fi.vm.yti.comments.api.jpa;

import java.time.LocalDate;
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

    Set<CommentRound> findByOrganizationsIdAndStatus(final UUID id,
                                                     final String status);

    Set<CommentRound> findByOrganizationsId(final UUID id);

    Set<CommentRound> findByStatus(final String status);

    Set<CommentRound> findByStatusAndEndDateBefore(final String status, final LocalDate now);

    Set<CommentRound> findByStatusAndStartDateAfter(final String status, final LocalDate now);

    Set<CommentRound> findBySourceContainerType(final String containerType);

    Set<CommentRound> findByOrganizationsIdAndStatusAndSourceContainerType(final UUID id,
                                                                           final String status,
                                                                           final String containerType);

    Set<CommentRound> findByOrganizationsIdAndSourceContainerType(final UUID id,
                                                                  final String containerType);

    Set<CommentRound> findByStatusAndSourceContainerType(final String status,
                                                         final String containerType);
}
