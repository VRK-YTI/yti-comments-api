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

    CommentThread findByCommentRoundIdAndCommentThreadIdentifier(final UUID commentRoundId,
                                                                 final String commentThreadIdentifier);

    CommentThread findByCommentRoundIdAndSequenceId(final UUID commentThreadId,
                                                    final Integer commentThreadSequenceId);

    CommentThread findByCommentRoundAndId(final CommentRound commentRound,
                                          final UUID commentThreadId);

    Set<CommentThread> findByCommentRoundId(final UUID commentRoundId);

    Set<CommentThread> findByCommentRoundUriIn(final Set<String> commentRoundUris,
                                               final LocalDateTime after,
                                               final LocalDateTime before,
                                               final PageRequest pageRequest);

    Set<CommentThread> findByUriIn(final Set<String> uris,
                                   final LocalDateTime after,
                                   final LocalDateTime before,
                                   final PageRequest pageRequest);

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

    int getCommentThreadCount(final Set<String> commentThreadUris,
                              final Set<String> commentRoundUris,
                              final LocalDateTime after,
                              final LocalDateTime before);
}
