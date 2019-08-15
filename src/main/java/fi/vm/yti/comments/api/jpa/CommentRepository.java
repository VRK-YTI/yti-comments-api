package fi.vm.yti.comments.api.jpa;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import fi.vm.yti.comments.api.entity.Comment;

@Repository
@Transactional
public interface CommentRepository extends PagingAndSortingRepository<Comment, String> {

    Comment findById(final UUID commentId);

    Set<Comment> findByCommentThreadCommentRoundIdAndUserIdAndParentCommentIsNull(final UUID commentRoundId,
                                                                                  final UUID userId);

    Comment findByCommentThreadIdAndUserIdAndParentCommentIsNull(final UUID commentRoundId,
                                                                 final UUID userId);

    Set<Comment> findByCommentThreadIdOrderByCreatedAsc(final UUID commentThreadId);

    Set<Comment> findByCommentThreadIdAndParentCommentIsNullOrderByCreatedAsc(final UUID commentThreadId);

    Set<Comment> findAll();

    Set<Comment> findByParentComment(final Comment comment);
}
