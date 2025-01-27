package org.efrenjm.investingtracker.repository;

import org.efrenjm.investingtracker.model.Transaction.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
}
