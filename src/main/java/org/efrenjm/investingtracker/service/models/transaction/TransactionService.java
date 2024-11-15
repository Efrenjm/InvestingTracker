package org.efrenjm.investingtracker.service.models.transaction;

import org.efrenjm.investingtracker.model.Transaction.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Flux<Transaction> getAllTransactions();

    Mono<Transaction> getTransactionById(String id);

    Mono<Transaction> createTransaction(Transaction transaction);

    Mono<Transaction> updateTransaction(String id, Transaction transaction);

    Mono<Void> deleteTransaction(String id);
}
