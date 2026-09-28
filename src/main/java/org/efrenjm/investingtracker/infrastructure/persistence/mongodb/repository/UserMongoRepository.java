package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.repository;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.user.UserEntity;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.AccountSummaryProjection;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.PublicProfileProjection;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.SelfProfileProjection;
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

    @Query("{ $or: [ { email: ?0 }, { phone_number: ?0 }, { username: ?0 } ] }")
    Mono<UserEntity> findByAnyCredential(String credential);

    @Query("{ $or: [ { email: ?0 }, { 'verification_request.credential': ?0 } ] }")
    Mono<UserEntity> findEmailInUse(String username);

    @Query("{ $or: [ { phone_number: ?0 }, { 'verification_request.credential': ?0 } ] }")
    Mono<UserEntity> findPhoneInUse(String username);

    @Query("{ _id: ?0 }")
    Mono<SelfProfileProjection> fetchProfile(String userId);

    @Aggregation({
        "{ $match: { _id: ?0 } }",
        "{ $lookup: { from: 'users', localField: 'friends', foreignField: '_id', as: 'friendDetails' } }",
        "{ $unwind: { path: '$friendDetails', preserveNullAndEmptyArrays: false } }",
        "{ $project: {"
                + "_id: '$friendDetails._id',"
                + "username: '$friendDetails.username',"
                + "firstName: '$friendDetails.first_name',"
                + "middleName: '$friendDetails.middle_name',"
                + "lastName: '$friendDetails.last_name',"
                + "profilePicture: '$friendDetails.profile_picture'"
                + "} }"
    })
    Flux<PublicProfileProjection> fetchFriends(ObjectId userId);

    @Aggregation({
        "{ $match: { _id: ?0 } }",
        "{ $lookup: { from: 'users', localField: 'friendIds', foreignField: '_id', as: 'friends' } }",
        "{ $unwind: '$friends' }",
        "{ $replaceRoot: { newRoot: '$friends' } }",
        "{ $project: { "
                + "id: '$_id', "
                + "firstName: { $cond: { if: '$preferences.isNamePublic', then: '$first_name', else: '$$REMOVE' } }, "
                + "middleName: { $cond: { if: '$preferences.isNamePublic', then: '$middle_name', else: '$$REMOVE' } }, "
                + "lastName: { $cond: { if: '$preferences.isNamePublic', then: '$last_name', else: '$$REMOVE' } }, "
                + "email: { $cond: { if: '$preferences.isEmailPublic', then: '$email', else: '$$REMOVE' } }, "
                + "phone: { $cond: { if: '$preferences.isPhonePublic', then: '$phone', else: '$$REMOVE' } } "
                + "} }"
    })
    Flux<PublicProfileProjection> findFriendsWithPrivacy(String userId);

    @Aggregation({
        "{ $match: { _id: ?0 } }",
        "{ $lookup: { from: 'wallets', localField: 'wallets', foreignField: '_id', as: 'wallets' } }",
        "{ $unwind: { path: '$wallets', preserveNullAndEmptyArrays: false } }",
        "{ $replaceRoot: { newRoot: '$wallets' } }",
        "{ $lookup: { from: 'users', localField: 'members.user', foreignField: '_id', as: 'users' } }"
    })
    Flux<WalletSummaryProjection> fetchWallets(ObjectId userId);

    @Aggregation({
        "{ $match: { _id: ?0 } }",
        "{ $lookup: { from: 'accounts', localField: 'wallets', foreignField: 'walletId', as: 'accounts' } }",
        "{ $unwind: { path: '$accounts', preserveNullAndEmptyArrays: false } }",
        "{ $replaceRoot: { newRoot: '$accounts' } }"
    })
    Flux<AccountSummaryProjection> fetchAccounts(ObjectId userId);
}
