package org.efrenjm.investingtracker.repository;

import org.efrenjm.investingtracker.model.role.Role;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends ReactiveMongoRepository<Role, String>
{ }
