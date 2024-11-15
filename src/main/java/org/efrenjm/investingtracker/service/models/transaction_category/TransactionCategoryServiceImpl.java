package org.efrenjm.investingtracker.service.models.transaction_category;

import org.efrenjm.investingtracker.model.TransactionCategory.TransactionCategory;
import org.efrenjm.investingtracker.repository.TransactionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionCategoryServiceImpl implements TransactionCategoryService{
    @Autowired
    private TransactionCategoryRepository transactionCategoryRepository;

    @Override
    public Flux<TransactionCategory> getAllTransactionCategories() {
        return transactionCategoryRepository.findAll();
    }

    @Override
    public Mono<TransactionCategory> getTransactionCategoryById(String id) {
        return transactionCategoryRepository.findById(id);
    }

    @Override
    public Mono<TransactionCategory> createTransactionCategory(TransactionCategory transactionCategory) {
        return transactionCategoryRepository.insert(transactionCategory);
    }

    @Override
    public Mono<TransactionCategory> updateTransactionCategory(String id, TransactionCategory transactionCategory) {
        transactionCategory.setId(id);
        return transactionCategoryRepository.save(transactionCategory);
    }

    @Override
    public Mono<Void> deleteTransactionCategory(String id) {
        return transactionCategoryRepository.deleteById(id);
    }
}
