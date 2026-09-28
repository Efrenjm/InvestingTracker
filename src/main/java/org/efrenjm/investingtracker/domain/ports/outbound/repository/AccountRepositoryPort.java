package org.efrenjm.investingtracker.domain.ports.outbound.repository;

import java.util.List;
import org.efrenjm.investingtracker.domain.model.account.BaseAccount;
import reactor.core.publisher.Mono;

public interface AccountRepositoryPort {
    Mono<BaseAccount> save(BaseAccount account);

    Mono<BaseAccount> findById(String accountId);

    List<BaseAccount> findAll();
}
