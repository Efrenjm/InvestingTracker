package org.efrenjm.investingtracker.service.models.transaction_category;

import org.efrenjm.investingtracker.model.TransactionCategory.TransactionCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionCategoryService {
    Flux<TransactionCategory> getAllTransactionCategories();

    Mono<TransactionCategory> getTransactionCategoryById(String id);

    Mono<TransactionCategory> createTransactionCategory(TransactionCategory transactionCategory);

    Mono<TransactionCategory> updateTransactionCategory(String id, TransactionCategory transactionCategory);

    Mono<Void> deleteTransactionCategory(String id);
}
