package fi.vm.yti.comments.api.dao;

import java.util.UUID;

import fi.vm.yti.comments.api.entity.Organization;

public interface OrganizationDao {

    Organization findById(final UUID organizationId);
}
