package fi.vm.yti.comments.api.dao;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.GlobalCommentsDTO;
import fi.vm.yti.comments.api.entity.GlobalComments;

public interface GlobalCommentsDao {

    Set<GlobalComments> findAll();

    GlobalComments findById(final UUID globalCommentsId);

    GlobalComments addOrUpdateGlobalCommentsFromDto(final GlobalCommentsDTO globalCommentsDto);

    Set<GlobalComments> addOrUpdateGlobalCommentsFromDtos(final Set<GlobalCommentsDTO> globalCommentsDtos);
}
