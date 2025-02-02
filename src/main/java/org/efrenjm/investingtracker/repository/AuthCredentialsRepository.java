package org.efrenjm.investingtracker.repository;

import org.efrenjm.investingtracker.model.Auth.AuthCredentials;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthCredentialsRepository extends ReactiveMongoRepository<AuthCredentials, String> {
	Mono<UserDetails> findByEmailOrPhoneNumber(String email, String phone);

	Mono<UserDetails> findByUsername(String username);

	Mono<Boolean> existsByEmail(String email);

	Mono<Boolean> existsByPhoneNumber(String phoneNumber);
}
