package fi.vm.yti.comments.api.dao;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;

import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;

public interface CommentThreadDao {

    void saveAll(final Set<CommentThread> commentThreads);

    void delete(final CommentThread commentThread);

    Set<CommentThread> findAll();

    Set<CommentThread> findAll(final PageRequest pageRequest);

    CommentThread findById(final UUID commentThreadId);

    CommentThread findByCommentRoundAndId(final CommentRound commentRound,
                                          final UUID commentThreadId);

    Set<CommentThread> findByCommentRoundId(final UUID commentRoundId);

    Set<CommentThread> findByIds(final Set<UUID> uuids);

    CommentThread addOrUpdateCommentThreadFromDto(final CommentRound commentRound,
                                                  final CommentThreadDTO commentThreadDto);

    Set<CommentThread> addOrUpdateCommentThreadsFromDtos(final CommentRound commentRound,
                                                         final Set<CommentThreadDTO> commentThreadDtos);

    Set<CommentThread> addOrUpdateCommentThreadsFromDtos(final CommentRound commentRound,
                                                         final Set<CommentThreadDTO> commentThreadDtos,
                                                         final boolean removeOrphans);

    void deleteCommentThread(final CommentThread commentThread);

    void updateCommentsModified(final UUID commentThreadId,
                                final LocalDateTime timeStamp);

    int getCommentThreadCount(final Set<UUID> commentThreadIds,
                              final UUID commentRoundId,
                              final LocalDateTime after,
                              final LocalDateTime before);
}
