package fi.vm.yti.comments.api.service;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.entity.CommentThread;

public interface CommentThreadService {

    Set<CommentThreadDTO> findAll();

    CommentThreadDTO findById(final UUID commentThreadId);

    Set<CommentThreadDTO> findByCommentRoundId(final UUID commentRoundId);

    CommentThreadDTO addOrUpdateCommentThreadFromDto(final UUID commentRoundId,
                                                     final CommentThreadDTO fromCommentThread);

    Set<CommentThreadDTO> addOrUpdateCommentThreadsFromDtos(final UUID commentRoundId,
                                                            final Set<CommentThreadDTO> fromCommentThreads,
                                                            final boolean removeOrphans);

    void deleteCommentThread(final CommentThread commentThread);
}
