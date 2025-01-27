package org.efrenjm.investingtracker.repository;

import org.efrenjm.investingtracker.model.Auth.Auth;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthRepository extends ReactiveMongoRepository<Auth, String> {
	Mono<UserDetails> findByUsername(String username);
}
