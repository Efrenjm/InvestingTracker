package org.efrenjm.investingtracker.repository;

import org.efrenjm.investingtracker.model.Rule.Rule;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends ReactiveMongoRepository<Rule, String>
{ }
