package org.efrenjm.investingtracker.repository;

import org.efrenjm.investingtracker.model.Organization.Organization;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends ReactiveMongoRepository<Organization, String>
{ }
