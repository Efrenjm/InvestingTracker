package org.efrenjm.investingtracker.infrastructure.persistence.mongodb;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.WalletEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletMongoRepository extends ReactiveMongoRepository<WalletEntity, ObjectId> {
}
