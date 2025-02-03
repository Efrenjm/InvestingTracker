package org.efrenjm.investingtracker.repository;

import org.efrenjm.investingtracker.model.rule.Rule;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends ReactiveMongoRepository<Rule, String>
{ }
