package org.efrenjm.investingtracker.repository;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends ReactiveMongoRepository<Organization, ObjectId>
{ }
