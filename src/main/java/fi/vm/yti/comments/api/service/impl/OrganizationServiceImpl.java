package fi.vm.yti.comments.api.service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.base.Stopwatch;

import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.dto.GroupManagementOrganizationDTO;
import fi.vm.yti.comments.api.dto.OrganizationDTO;
import fi.vm.yti.comments.api.entity.Organization;
import fi.vm.yti.comments.api.jpa.OrganizationRepository;
import fi.vm.yti.comments.api.service.OrganizationService;

@Singleton
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceImpl.class);
    private final OrganizationRepository organizationRepository;
    private final DtoMapper dtoMapper;

    @Inject
    public OrganizationServiceImpl(final OrganizationRepository organizationRepository,
                                   final DtoMapper dtoMapper) {
        this.organizationRepository = organizationRepository;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public Set<OrganizationDTO> findAll() {
        return dtoMapper.mapOrganizations(organizationRepository.findAll(), true);
    }

    @Transactional
    public Set<OrganizationDTO> findByRemovedIsFalse(final boolean hasCommentRounds) {
        Set<Organization> organizations = null;
        if (hasCommentRounds) {
            organizations = organizationRepository.findByRemovedIsFalseAndCommentRoundsIsNotNull();
        } else {
            organizations = organizationRepository.findByRemovedIsFalse();
        }
        return dtoMapper.mapOrganizations(organizations, true);
    }

    @Transactional
    public OrganizationDTO findById(final UUID organizationId) {
        return dtoMapper.mapOrganization(organizationRepository.findById(organizationId), true);
    }

    @Transactional
    public Set<OrganizationDTO> parseAndPersistGroupManagementOrganizationsFromJson(final String jsonPayload) {
        final Stopwatch watch = Stopwatch.createStarted();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        Set<GroupManagementOrganizationDTO> groupManagementOrganizations = new HashSet<>();
        try {
            groupManagementOrganizations = mapper.readValue(jsonPayload, new TypeReference<Set<GroupManagementOrganizationDTO>>() {
            });
            LOG.info("Organization data loaded: " + groupManagementOrganizations.size() + " Organizations in " + watch);
            watch.reset().start();
        } catch (final IOException e) {
            LOG.error("Organization fetching and processing failed!", e);
        }
        final Set<Organization> organizations = new HashSet<>();
        for (final GroupManagementOrganizationDTO groupManagementOrganization : groupManagementOrganizations) {
            final Organization organization = createOrUpdateOrganizationFromGroupManagementOrganizationDto(groupManagementOrganization);
            organizations.add(organization);
        }
        if (!organizations.isEmpty()) {
            organizationRepository.saveAll(organizations);
        }
        return dtoMapper.mapOrganizations(organizations, true);
    }

    private Organization createOrUpdateOrganizationFromGroupManagementOrganizationDto(final GroupManagementOrganizationDTO groupManagementOrganizationDto) {
        final Organization existingOrganization = organizationRepository.findById(groupManagementOrganizationDto.getUuid());
        final Organization organization;
        if (existingOrganization != null) {
            existingOrganization.setUrl(groupManagementOrganizationDto.getUrl());
            existingOrganization.setPrefLabel(groupManagementOrganizationDto.getPrefLabel());
            existingOrganization.setDescription(groupManagementOrganizationDto.getDescription());
            existingOrganization.setRemoved(groupManagementOrganizationDto.getRemoved());
            organization = existingOrganization;
        } else {
            organization = new Organization();
            organization.setId(groupManagementOrganizationDto.getUuid());
            organization.setUrl(groupManagementOrganizationDto.getUrl());
            organization.setPrefLabel(groupManagementOrganizationDto.getPrefLabel());
            organization.setDescription(groupManagementOrganizationDto.getDescription());
            organization.setRemoved(groupManagementOrganizationDto.getRemoved());
        }
        return organization;
    }
}
