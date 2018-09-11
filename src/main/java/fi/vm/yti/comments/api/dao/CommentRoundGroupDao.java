package fi.vm.yti.comments.api.dao;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.CommentRoundGroupDTO;
import fi.vm.yti.comments.api.entity.CommentRoundGroup;

public interface CommentRoundGroupDao {

    Set<CommentRoundGroup> findAll();

    CommentRoundGroup findById(final UUID commentRoundGroupId);

    CommentRoundGroup addOrUpdateCommentRoundGroupFromDto(final CommentRoundGroupDTO commentRoundGroupDto);

    Set<CommentRoundGroup> addOrUpdateCommentRoundGroupsFromDtos(final Set<CommentRoundGroupDTO> commentRoundGroupDtos);
}
