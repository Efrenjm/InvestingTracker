package org.efrenjm.investingtracker.infrastructure.persistence.mongodb;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionMongoRepository extends ReactiveMongoRepository<TransactionEntity, ObjectId> {
//	public Flux<TransactionSummary> fetchAccountTransactions(ObjectId accountId, Date startDate, Date endDate, int page, int size) {
//		Pageable pageable = PageRequest.of(page, size);
//
//		Criteria criteria = new Criteria().andOperator(
//				new Criteria().orOperator(
//						Criteria.where("to_accounts.account").is(accountId),
//						Criteria.where("from_accounts.account").is(accountId)
//				),
//				Criteria.where("transaction_date").gte(startDate).lte(endDate)
//		);
//
//		Aggregation aggregation = Aggregation.newAggregation(
//				Aggregation.match(criteria),
//				Aggregation.sort(Sort.Direction.DESC, "transaction_date"),
//				Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()),
//				Aggregation.limit(pageable.getPageSize())
//		);
//
//		return reactiveMongoTemplate.aggregate(aggregation, "transactions", TransactionSummary.class);
//	}
}
