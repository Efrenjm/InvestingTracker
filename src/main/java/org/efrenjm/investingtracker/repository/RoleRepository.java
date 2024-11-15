package org.efrenjm.investingtracker.repository;

import org.efrenjm.investingtracker.model.Role.Role;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends ReactiveMongoRepository<Role, String>
{ }
