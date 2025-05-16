package org.efrenjm.investingtracker.infrastructure.persistence.mongodb;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.UserEntity;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.AccountSummaryProjection;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.PublicProfileProjection;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.WalletSummaryProjection;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserMongoRepository extends ReactiveMongoRepository<UserEntity, ObjectId> {
	Mono<UserEntity> findByUsername(String username);

	Mono<UserEntity> findByEmail(String email);

	Mono<UserEntity> findByPhoneNumber(String phone);

	@Query("{ $or: [{ email: ?0 }, { phone: ?0 }, { username: ?0 }]}")
	Mono<UserEntity> findByAnyCredential(String credential);

	@Query("{ $or: [ { 'email': ?0 }, { 'verificationRequest.credential': ?0 } ] }")
	Mono<UserEntity> findEmailInUse(String username);

	@Query("{ $or: [ { 'phone_number': ?0 }, { 'verificationRequest.credential': ?0 } ] }")
	Mono<UserEntity> findPhoneInUse(String username);

	@Aggregation(pipeline= {
			"{ $match: { _id: ?0 } }",
			"{ $lookup: { from: 'users', localField: 'friends', foreignField: '_id', as: 'friendDetails' } }",
			"{ $unwind: { path: '$friendDetails', preserveNullAndEmptyArrays: false } }",
			"{ $project: {" +
					"_id: '$friendDetails._id'," +
					"username: '$friendDetails.username'," +
					"firstName: '$friendDetails.first_name'" +
					"middleName: '$friendDetails.middle_name'," +
					"lastName: '$friendDetails.last_name'," +
					"profilePicture: '$friendDetails.profile_picture'" +
			"} }"
	})
	Flux<PublicProfileProjection> fetchFriends(ObjectId userId);


	@Aggregation(pipeline= {
			"{ $match: { _id: ?0 } }",
			"{ $lookup: { from: 'wallets', localField: 'wallets', foreignField: '_id', as: 'wallets' } }",
			"{ $unwind: { path: '$wallets', preserveNullAndEmptyArrays: false } }",
			"{ $replaceRoot: { newRoot: '$wallets' } }",
			"{ $lookup: { from: 'users', localField: 'members.user', foreignField: '_id', as: 'users' } }"
	})
	Flux<WalletSummaryProjection> fetchWallets(ObjectId userId);

	@Aggregation(pipeline= {
			"{ $match: { _id: ?0 } }",
			"{ $lookup: { from: 'accounts', localField: 'wallets', foreignField: 'walletId', as: 'accounts' } }",
			"{ $unwind: { path: '$accounts', preserveNullAndEmptyArrays: false } }",
			"{ $replaceRoot: { newRoot: '$accounts' } }"
	})
	Flux<AccountSummaryProjection> fetchAccounts(ObjectId userId);
}
