package org.efrenjm.investingtracker.repository;

import org.efrenjm.investingtracker.model.Account.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends ReactiveMongoRepository<Account, String>
{ }
