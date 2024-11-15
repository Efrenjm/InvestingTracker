package org.efrenjm.investingtracker.service.models.organization;

import org.efrenjm.investingtracker.model.Organization.Organization;
import org.efrenjm.investingtracker.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public Flux<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @Override
    public Mono<Organization> getOrganizationById(String id) {
        return organizationRepository.findById(id);
    }

    @Override
    public Mono<Organization> createOrganization(Organization organization) {
        return organizationRepository.insert(organization);
    }

    @Override
    public Mono<Organization> updateOrganization(String id, Organization organization) {
        organization.setId(id);
        return organizationRepository.save(organization);
    }

    @Override
    public Mono<Void> deleteOrganization(String id) {
        return organizationRepository.deleteById(id);
    }
}
