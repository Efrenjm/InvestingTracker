package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.account.BaseAccount;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.AccountRepositoryPort;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.account.AccountEntity;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.repository.AccountMongoRepository;
import org.efrenjm.investingtracker.infrastructure.persistence.utils.MongoUtils;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {
    private final AccountMongoRepository accountMongoRepository;

    @Override
    public Mono<BaseAccount> save(BaseAccount account) {
        return accountMongoRepository
                .save(AccountEntity.fromDomain(account))
                .map(AccountEntity::toDomain);
    }

    @Override
    public Mono<BaseAccount> findById(String accountId) {
        ObjectId id = MongoUtils.idToEntity(accountId);
        if (id == null) {
            return Mono.empty();
        }
        return accountMongoRepository.findById(id).map(AccountEntity::toDomain);
    }

    @Override
    public List<BaseAccount> findAll() {
        return List.of();
    }
}
