package fi.vm.yti.comments.api.dao;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;

public interface CommentDao {

    Set<Comment> findAll();

    Comment findById(final UUID commentId);

    Set<Comment> findCommentRoundMainLevelCommentsForUserId(final UUID commentRoundId,
                                                            final UUID userId);

    Set<Comment> findByCommentThreadId(final UUID commentRoundId);

    Set<Comment> findByCommentThreadIdAndParentCommentIsNull(final UUID commentRoundId);

    Comment addOrUpdateCommentFromDto(final CommentThread commentThread,
                                      final CommentDTO commentDto);

    Set<Comment> addOrUpdateCommentsFromDtos(final CommentThread commentThread,
                                             final Set<CommentDTO> commentDtos);

    Set<Comment> addOrUpdateCommentsFromDtos(final CommentRound commentRound,
                                             final Set<CommentDTO> commentDtos);
}
