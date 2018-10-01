package fi.vm.yti.comments.api.dao.impl;

import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.OrganizationDao;
import fi.vm.yti.comments.api.entity.Organization;
import fi.vm.yti.comments.api.jpa.OrganizationRepository;

@Component
public class OrganizationDaoImpl implements OrganizationDao {

    private OrganizationRepository organizationRepository;

    @Inject
    public OrganizationDaoImpl(final OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public Organization findById(final UUID organizationId) {
        return organizationRepository.findById(organizationId);
    }
}
