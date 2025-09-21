package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.repository;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.wallet.WalletEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletMongoRepository extends ReactiveMongoRepository<WalletEntity, ObjectId> {
}
