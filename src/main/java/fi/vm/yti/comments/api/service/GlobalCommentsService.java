package fi.vm.yti.comments.api.service;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.GlobalCommentsDTO;

public interface GlobalCommentsService {

    Set<GlobalCommentsDTO> findAll();

    GlobalCommentsDTO findById(final UUID commentId);

    GlobalCommentsDTO addOrUpdateGlobalCommentsFromDto(final GlobalCommentsDTO fromGlobalComments);

    Set<GlobalCommentsDTO> addOrUpdateGlobalCommentsFromDtos(final Set<GlobalCommentsDTO> fromGlobalComments);
}
