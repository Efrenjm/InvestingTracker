package org.efrenjm.investingtracker.service.models.rule;

import org.efrenjm.investingtracker.model.Rule.Rule;
import org.efrenjm.investingtracker.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RuleServiceImpl implements RuleService {
    @Autowired
    private RuleRepository ruleRepository;

    @Override
    public Flux<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    @Override
    public Mono<Rule> getRuleById(String id) {
        return ruleRepository.findById(id);
    }

    @Override
    public Mono<Rule> createRule(Rule rule) {
        return ruleRepository.insert(rule);
    }

    @Override
    public Mono<Rule> updateRule(String id, Rule rule) {
        rule.setId(id);
        return ruleRepository.save(rule);
    }

    @Override
    public Mono<Void> deleteRule(String id) {
        return ruleRepository.deleteById(id);
    }
}
