package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.repository;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.account.AccountEntity;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.AccountSummaryProjection;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccountMongoRepository extends ReactiveMongoRepository<AccountEntity, ObjectId> {
    @Query(
            value = "{ 'wallet_id': ?0 }",
            fields =
                    "{ '_id': 1, 'name': 1, 'description': 1, 'type': 1, 'available': 1, 'tags': 1, 'account_config': 1 }")
    Flux<AccountSummaryProjection> findWalletAccounts(ObjectId walletId);
    //	public Flux<Account> fetchAccount(ObjectId accountId, ObjectId userId) {
    //		Aggregation aggregation = Aggregation.newAggregation(
    //				Aggregation.unwind("accounts"),
    //				Aggregation.match(
    //						Criteria.where("accounts._id").is(accountId)
    //								.and("members.user").is(userId)),
    //				Aggregation.replaceRoot("accounts"));
    //
    //		return reactiveMongoTemplate.aggregate(aggregation, "wallets", Account.class);
    //	}

    /* TODO: Analysis of this function */
    //	public Mono<Account> createAccount(Account account, ObjectId walletId) {
    //		Date now = new Date();
    //		account.setId(new ObjectId());
    //		account.setCreatedAt(now);
    //		account.setUpdatedAt(now);
    //
    //		return walletRepository.findById(walletId)
    //				.flatMap(wallet -> {
    //					wallet.getAccounts().add(account);
    //					return walletRepository.save(wallet);
    //				})
    //				.map(wallet -> wallet.getAccounts().getLast());
    //	}
}
