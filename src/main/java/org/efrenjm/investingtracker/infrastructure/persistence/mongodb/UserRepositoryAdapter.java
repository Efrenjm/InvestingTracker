package org.efrenjm.investingtracker.infrastructure.persistence.mongodb;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.dto.AccountSummary;
import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.UserEntity;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.AccountSummaryProjection;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.PublicProfileProjection;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.WalletSummaryProjection;
import org.efrenjm.investingtracker.infrastructure.security.SecurityUser;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
	private final UserMongoRepository userMongoRepository;

	@Override
	public Mono<User> findById(String userId) {
		return userMongoRepository.findById(new ObjectId(userId))
				.map(UserEntity::toDomain);
	}

	@Override
	public Mono<SecurityUser> findByUsername(String username) {
		return userMongoRepository.findByUsername(username)
				.map(user -> new SecurityUser(user.toDomain()));
	}

	@Override
	public Mono<User> findByAnyCredential(String credential) {
		return userMongoRepository.findByAnyCredential(credential)
				.map(UserEntity::toDomain);
	}

	@Override
	public Mono<User> save(User user) {
		return userMongoRepository.save(UserEntity.fromDomain(user))
				.map(UserEntity::toDomain);
	}

	@Override
	public Mono<Void> delete(String userId) {
		return userMongoRepository.deleteById(new ObjectId(userId));
	}

	@Override
	public Mono<User> findEmailInUse(String email) {
		return userMongoRepository.findEmailInUse(email)
				.map(UserEntity::toDomain);
	}

	@Override
	public Mono<User> findPhoneInUse(String phone) {
		return userMongoRepository.findPhoneInUse(phone)
				.map(UserEntity::toDomain);
	}

	@Override
	public Flux<AccountSummary> fetchAccounts(String userId) {
		return userMongoRepository.fetchAccounts(new ObjectId(userId))
				.map(AccountSummaryProjection::toDomain);
	}

	public Flux<PublicProfile> fetchFriends(String userId) {
		return userMongoRepository.fetchFriends(new ObjectId(userId))
				.map(PublicProfileProjection::toDomain);
	}

	public Flux<WalletSummary> fetchWallets(String userId) {
		return userMongoRepository.fetchWallets(new ObjectId(userId))
				.map(WalletSummaryProjection::toDomain);
	}
}
