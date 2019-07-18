package fi.vm.yti.comments.api.dao;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;

public interface CommentThreadDao {

    void delete(final CommentThread commentThread);

    Set<CommentThread> findAll();

    CommentThread findById(final UUID commentThreadId);

    CommentThread findByCommentRoundAndId(final CommentRound commentRound,
                                          final UUID commentThreadId);

    Set<CommentThread> findByCommentRoundId(final UUID commentRoundId);

    CommentThread addOrUpdateCommentThreadFromDto(final CommentRound commentRound,
                                                  final CommentThreadDTO commentThreadDto);

    Set<CommentThread> addOrUpdateCommentThreadsFromDtos(final CommentRound commentRound,
                                                         final Set<CommentThreadDTO> commentThreadDtos);

    Set<CommentThread> addOrUpdateCommentThreadsFromDtos(final CommentRound commentRound,
                                                         final Set<CommentThreadDTO> commentThreadDtos,
                                                         final boolean removeOrphans);

    void deleteCommentThread(final CommentThread commentThread);
}
