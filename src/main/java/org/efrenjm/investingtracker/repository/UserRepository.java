package org.efrenjm.investingtracker.repository;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.user.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, ObjectId> {
	Mono<User> findByEmailAndPhoneNumber(String email, String phone);

	Mono<User> findByEmail(String email);

	Mono<User> findByPhoneNumber(String phone);

	Mono<Boolean> existsByEmailOrPhoneNumber(String email, String phone);

	Flux<User> findAllByUpdateEmailRequestOrUpdatePhoneRequest(String email, String phone);

	Mono<User> findByEmailOrUpdateEmailRequest(String email, String emailRequest);

	Mono<User> findByPhoneNumberOrUpdatePhoneRequest(String phoneNumber, String phoneNumberRequest);
}
