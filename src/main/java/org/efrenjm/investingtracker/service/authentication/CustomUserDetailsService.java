package org.efrenjm.investingtracker.service.authentication;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.user.User;
import org.efrenjm.investingtracker.service.model.user.UserService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements ReactiveUserDetailsService {

	private final UserService userService;

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return null;
	}

	public Mono<User> findById(ObjectId userId) {
		return userService.fetchUser(userId);
	}
}
