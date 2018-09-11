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

    Set<Comment> findByCommentRoundIdOrderByCreatedAsc(final UUID commentRoundId);

    Set<Comment> findByGlobalCommentsIdOrderByCreatedAsc(final UUID globalCommentsId);

    Set<Comment> findAll();
}
