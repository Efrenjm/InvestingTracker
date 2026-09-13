package org.efrenjm.investingtracker.service.model.user;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.dto.model.account.AccountSummary;
import org.efrenjm.investingtracker.dto.model.organization.OrganizationSummary;
import org.efrenjm.investingtracker.exception.authentication.MissingCredentialsException;
import org.efrenjm.investingtracker.exception.user_management.UserNotFoundException;
import org.efrenjm.investingtracker.model.user.User;
import org.efrenjm.investingtracker.dto.model.user.PublicProfile;
import org.efrenjm.investingtracker.repository.UserRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService /*implements IUserService*/{
	private final ReactiveMongoTemplate reactiveMongoTemplate;
	private final UserRepository userRepository;

	public Mono<User> fetchUser(ObjectId userId) {
		return userRepository.findById(userId)
				.switchIfEmpty(Mono.error(new UserNotFoundException(userId.toString())));
	}

	public Flux<PublicProfile> fetchFriends(ObjectId userId) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("_id").is(userId)),
				Aggregation.unwind("friends"),
				Aggregation.lookup("users", "friends", "_id", "friendDetails"),
				Aggregation.unwind("friendDetails"),
				Aggregation.project()
						.and("friendDetails._id").as("id")
						.and("friendDetails.username").as("username")
						.and("friendDetails.first_name").as("firstName")
						.and("friendDetails.middle_name").as("middleName")
						.and("friendDetails.last_name").as("lastName")
						.and("friendDetails.profile_picture").as("profilePicture")
		);

		return reactiveMongoTemplate.aggregate(aggregation, "users", PublicProfile.class);
	}

	public Flux<OrganizationSummary> fetchOrganizations(ObjectId userId) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("_id").is(userId)),
				Aggregation.lookup("organizations", "organizations", "_id", "organizations"),
				Aggregation.unwind("organizations"),
				Aggregation.replaceRoot("organizations"),
				Aggregation.lookup("users", "members.user", "_id", "users")
		);

		return reactiveMongoTemplate.aggregate(aggregation, "users", OrganizationSummary.class);
	}

	public Flux<AccountSummary> fetchAccounts(ObjectId userId) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("_id").is(userId)),
				Aggregation.lookup("organizations", "organizations", "_id", "organizations"),
				Aggregation.unwind("organizations"),
				Aggregation.unwind("organizations.accounts"),
				Aggregation.replaceRoot("organizations.accounts"),
				Aggregation.project()
						.and("_id").as("id")
						.and("name").as("name")
						.and("description").as("description")
						.and("type").as("type")
						.and("available").as("available")
						.and("tags").as("tags")
						.and("account_config").as("accountConfig")
		);

		return reactiveMongoTemplate.aggregate(aggregation, "users", AccountSummary.class);
	}


	public Mono<User> fetchUserByEmailOrPhone(String email, String phone) {
		if (email != null && phone != null) {
			return userRepository.findByEmailAndPhoneNumber(email, phone);
		} else if (email != null) {
			return userRepository.findByEmail(email);
		} else if (phone != null) {
			return userRepository.findByPhoneNumber(phone);
		} else {
			return Mono.error(new MissingCredentialsException());
		}
	}

	public Mono<Boolean> isEmailOrPhoneTaken(String email, String phone) {
		Mono<User> findUser;
		if (email != null && phone != null) {
			return userRepository.existsByEmailOrPhoneNumber(email, phone)
					.flatMap(exists -> {
						if (exists) {
							return Mono.just(true);
						}

						return userRepository.findAllByUpdateEmailRequestOrUpdatePhoneRequest(email, phone)
								.flatMap(this::isRequestStillValid)
								.any(Boolean::booleanValue)
								.defaultIfEmpty(false);
					});
		} else if (email != null) {
			findUser = userRepository.findByEmailOrUpdateEmailRequest(email, email);
		} else if (phone != null) {
			findUser = userRepository.findByPhoneNumberOrUpdatePhoneRequest(phone, phone);
		} else {
			return Mono.error(new MissingCredentialsException());
		}

		if (findUser != null) {
			return findUser.flatMap(user -> {
				String phoneRequest = user.getUpdatePhoneRequest();
				String emailRequest = user.getUpdateEmailRequest();
				if ((emailRequest != null && emailRequest.equals(email))
					|| (phoneRequest != null && phoneRequest.equals(phone))
				) {
					return isRequestStillValid(user);
				}
				return Mono.just(true);
			}).defaultIfEmpty(false);
		}

		return Mono.just(false);
	}

	public Mono<User> saveUser(User user) {
		Date now = new Date();

		user.setUpdatedAt(now);
		return userRepository.save(user);
	}

	public Mono<Void> deleteUser(ObjectId userId) {
		return userRepository.deleteById(userId);
	}

	private Mono<Boolean> isRequestStillValid(User user) {
		Date now = new Date();

		if (user.getCodeExpiration() != null && user.getCodeExpiration().before(now)) {
			if (user.isNewUser()) {
				return deleteUser(user.getId())
						.thenReturn(false);
			} else {
				return clearUpdateCredentialsRequest(user)
						.thenReturn(false);
			}
		}

		return Mono.just(true);
	}

	private Mono<User> clearUpdateCredentialsRequest(User user) {
		user.setUpdateEmailRequest(null);
		user.setUpdatePhoneRequest(null);
		user.setVerificationCode(null);
		user.setCodeExpiration(null);
		user.setCodeUsage(null);

		return userRepository.save(user);
	}
}
