package org.efrenjm.investingtracker.repository;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthCredentialsRepository extends ReactiveMongoRepository<AuthCredentials, ObjectId> {
	Mono<AuthCredentials> findByEmailAndPhoneNumber(String email, String phone);

	Mono<AuthCredentials> findByEmail(String email);

	Mono<AuthCredentials> findByPhoneNumber(String phone);

	Mono<Boolean> existsByEmailOrPhoneNumber(String email, String phone);

	Mono<Boolean> existsByEmail(String email);

	Mono<Boolean> existsByPhoneNumber(String phoneNumber);
}
