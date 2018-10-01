package fi.vm.yti.comments.api.service;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.OrganizationDTO;

public interface OrganizationService {

    Set<OrganizationDTO> findAll();

    Set<OrganizationDTO> findByRemovedIsFalse(final boolean hasCommentRounds);

    OrganizationDTO findById(final UUID organizationId);

    Set<OrganizationDTO> parseAndPersistGroupManagementOrganizationsFromJson(final String jsonPayload);
}
