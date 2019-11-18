package fi.vm.yti.comments.api.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;

import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.dto.ResourceDTO;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.error.Meta;

public interface CommentThreadService {

    Set<CommentThreadDTO> findAll();

    Set<CommentThreadDTO> findAll(final PageRequest pageable);

    CommentThreadDTO findById(final UUID commentThreadId);

    CommentThreadDTO findByCommentRoundIdAndCommentThreadIdentifier(final UUID commentRoundId,
                                                                    final String commentThreadIdentifier);

    Set<CommentThreadDTO> findByCommentRoundId(final UUID commentRoundId);

    CommentThreadDTO addOrUpdateCommentThreadFromDto(final UUID commentRoundId,
                                                     final CommentThreadDTO fromCommentThread);

    Set<CommentThreadDTO> addOrUpdateCommentThreadsFromDtos(final UUID commentRoundId,
                                                            final Set<CommentThreadDTO> fromCommentThreads,
                                                            final boolean removeOrphans);

    void deleteCommentThread(final CommentThread commentThread);

    Set<ResourceDTO> getResources(final Set<String> commentThreadUris,
                                  final Set<String> containerUri,
                                  final Meta meta);
}
