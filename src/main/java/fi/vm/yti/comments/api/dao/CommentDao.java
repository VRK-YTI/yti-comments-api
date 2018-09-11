package fi.vm.yti.comments.api.dao;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.entity.Comment;

public interface CommentDao {

    Set<Comment> findAll();

    Comment findById(final UUID commentId);

    Set<Comment> findByGlobalCommentsId(final UUID globalCommentsId);

    Set<Comment> findByCommentRoundId(final UUID commentRoundId);

    Comment addOrUpdateCommentFromDto(final CommentDTO commentDto);

    Set<Comment> addOrUpdateCommentsFromDtos(final Set<CommentDTO> commentDtos);
}
