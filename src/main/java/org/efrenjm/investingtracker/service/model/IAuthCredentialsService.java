package org.efrenjm.investingtracker.service.model;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import org.efrenjm.investingtracker.model.profile.Profile;
import reactor.core.publisher.Mono;

public interface IAuthCredentialsService {
	Mono<AuthCredentials> fetchUser(ObjectId userId);

	Mono<AuthCredentials> fetchUserByEmailOrPhone(String email, String phone);

	Mono<Boolean> existsUserByEmailOrPhone(String email, String phone);

	Mono<AuthCredentials> saveUser(AuthCredentials user);

	Mono<Profile> fetchUserProfile(ObjectId userId);
}
