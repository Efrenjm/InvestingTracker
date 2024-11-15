package org.efrenjm.investingtracker.service.models.transaction;

import org.efrenjm.investingtracker.model.Transaction.Transaction;
import org.efrenjm.investingtracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Flux<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Mono<Transaction> getTransactionById(String id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Mono<Transaction> createTransaction(Transaction transaction) {
        return transactionRepository.insert(transaction);
    }

    @Override
    public Mono<Transaction> updateTransaction(String id, Transaction transaction) {
        transaction.setId(id);
        return transactionRepository.save(transaction);
    }

    @Override
    public Mono<Void> deleteTransaction(String id) {
        return transactionRepository.deleteById(id);
    }
}
