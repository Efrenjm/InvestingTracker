package org.efrenjm.investingtracker.service.models.rule;

import org.efrenjm.investingtracker.model.Rule.Rule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RuleService {
    Flux<Rule> getAllRules();

    Mono<Rule> getRuleById(String id);

    Mono<Rule> createRule(Rule rule);

    Mono<Rule> updateRule(String id, Rule rule);

    Mono<Void> deleteRule(String id);
}
