package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.repository;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.wallet.Visibility;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.wallet.WalletEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface WalletMongoRepository extends ReactiveMongoRepository<WalletEntity, ObjectId> {
    Flux<WalletEntity> findByVisibility(Visibility visibility);
}
