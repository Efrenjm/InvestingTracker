package org.efrenjm.investingtracker.service.model;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.exception.authentication.MissingCredentialsException;
import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.repository.AuthCredentialsRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthCredentialsService implements IAuthCredentialsService{
	private final ReactiveMongoTemplate reactiveMongoTemplate;
	private final AuthCredentialsRepository authCredentialsRepository;

	public Mono<AuthCredentials> fetchUser(ObjectId userId) {
		return authCredentialsRepository.findById(userId);
	}

	public Mono<AuthCredentials> fetchUserByEmailOrPhone(String email, String phone) {
		if (email != null && phone != null) {
			return authCredentialsRepository.findByEmailAndPhoneNumber(email, phone);
		} else if (email != null) {
			return authCredentialsRepository.findByEmail(email);
		} else if (phone != null) {
			return authCredentialsRepository.findByPhoneNumber(phone);
		} else {
			return Mono.error(new MissingCredentialsException());
		}
	}

	public Mono<Boolean> existsUserByEmailOrPhone(String email, String phone) {
		if (email != null && phone != null) {
			return authCredentialsRepository.existsByEmailOrPhoneNumber(email, phone);
		} else if (email != null) {
			return authCredentialsRepository.existsByEmail(email);
		} else if (phone != null) {
			return authCredentialsRepository.existsByPhoneNumber(phone);
		} else {
			return Mono.error(new MissingCredentialsException());
		}
	}

	public Mono<AuthCredentials> saveUser(AuthCredentials user) {
		return authCredentialsRepository.save(user);
	}

	public Mono<Profile> fetchUserProfile(ObjectId userId) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("_id").is(userId)),
				Aggregation.lookup("profiles", "profile", "_id", "profile"),
				Aggregation.unwind("profile", true),
				Aggregation.replaceRoot("profile")
			);

		return reactiveMongoTemplate.aggregate(aggregation, "auth_credentials", Profile.class)
				.next();
	}
}
