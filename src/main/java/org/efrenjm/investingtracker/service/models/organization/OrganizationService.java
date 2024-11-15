package org.efrenjm.investingtracker.service.models.organization;

import org.efrenjm.investingtracker.model.Organization.Organization;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrganizationService {
    Flux<Organization> getAllOrganizations();

    Mono<Organization> getOrganizationById(String id);

    Mono<Organization> createOrganization(Organization organization);

    Mono<Organization> updateOrganization(String id, Organization organization);

    Mono<Void> deleteOrganization(String id);
}
