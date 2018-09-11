package fi.vm.yti.comments.api.service;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.CommentRoundGroupDTO;

public interface CommentRoundGroupService {

    Set<CommentRoundGroupDTO> findAll();

    CommentRoundGroupDTO findById(final UUID commentRoundGroupId);

    CommentRoundGroupDTO addOrUpdateCommentRoundGroupFromDto(final CommentRoundGroupDTO fromCommentRoundGroup);

    Set<CommentRoundGroupDTO> addOrUpdateCommentRoundGroupsFromDtos(final Set<CommentRoundGroupDTO> fromCommentRoundGroups);
}
