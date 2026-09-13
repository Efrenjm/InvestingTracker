package org.efrenjm.investingtracker.service.model.user;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.user.User;
import reactor.core.publisher.Mono;

public interface IUserService {
	Mono<User> fetchUser(ObjectId userId);

	Mono<User> fetchUserByEmailOrPhone(String email, String phone);

	Mono<Boolean> existsUserByEmailOrPhone(String email, String phone);

	Mono<User> saveUser(User user);

	Mono<User> fetchUserProfile(ObjectId userId);
}
