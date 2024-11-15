package org.efrenjm.investingtracker.repository;

import org.efrenjm.investingtracker.model.TransactionCategory.TransactionCategory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCategoryRepository extends ReactiveMongoRepository<TransactionCategory, String>
{ }
