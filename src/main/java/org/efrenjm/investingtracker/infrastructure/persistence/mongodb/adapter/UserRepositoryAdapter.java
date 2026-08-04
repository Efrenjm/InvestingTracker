package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.adapter;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.dto.AccountSummary;
import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.user.UserEntity;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.repository.UserMongoRepository;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.AccountSummaryProjection;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.WalletSummaryProjection;
import org.efrenjm.investingtracker.infrastructure.persistence.utils.MongoUtils;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort
{
	private final UserMongoRepository userMongoRepository;
//	private final ProjectionAdapter projectionAdapter;

	@Override
	public Mono<User> findById(String userId)
	{
		if (userId == null || userId.isBlank())
		{
			return Mono.empty();
		}
		ObjectId id = MongoUtils.idToEntity(userId);
		if (id == null)
		{
			return findByAnyCredential(userId);
		}
		return userMongoRepository
				.findById(id)
				.map(UserEntity::toDomain)
				.switchIfEmpty(Mono.defer(() -> findByAnyCredential(userId)));
	}

	@Override
	public Mono<User> findSecurityUser(String username)
	{
		if (username == null || username.isBlank())
		{
			return Mono.empty();
		}
		return userMongoRepository
				.findByUsername(username)
				.map(UserEntity::toDomain);
	}

	@Override
	public Mono<User> findByAnyCredential(String credential)
	{
		if (credential == null || credential.isBlank())
		{
			return Mono.empty();
		}
		return userMongoRepository
				.findByAnyCredential(credential)
				.map(UserEntity::toDomain);
	}

	@Override
	public Mono<User> save(User user)
	{
		return userMongoRepository
				.save(UserEntity.fromDomain(user))
				.map(UserEntity::toDomain);
	}

	@Override
	public Mono<Void> delete(String userId)
	{
		ObjectId id = MongoUtils.idToEntity(userId);
		if (id == null)
		{
			return Mono.empty();
		}
		return userMongoRepository.deleteById(id);
	}

	@Override
	public Mono<User> findEmailInUse(String email)
	{
		if (email == null || email.isBlank())
		{
			return Mono.empty();
		}
		return userMongoRepository
				.findEmailInUse(email)
				.map(UserEntity::toDomain);
	}

	@Override
	public Mono<User> findPhoneInUse(String phone)
	{
		if (phone == null || phone.isBlank())
		{
			return Mono.empty();
		}
		return userMongoRepository
				.findPhoneInUse(phone)
				.map(UserEntity::toDomain);
	}

	@Override
	public Flux<AccountSummary> fetchAccounts(String userId)
	{
		ObjectId id = MongoUtils.idToEntity(userId);
		if (id == null)
		{
			return Flux.empty();
		}
		return userMongoRepository
				.fetchAccounts(id)
				.map(AccountSummaryProjection::toDomain);
	}

	public Flux<PublicProfile> fetchFriends(String userId)
	{
		ObjectId id = MongoUtils.idToEntity(userId);
		if (id == null)
		{
			return Flux.empty();
		}
		return userMongoRepository.fetchFriends(id)
				.map(projection -> new PublicProfile(
						projection.get_id(),
						projection.getUsername(),
						projection.getEmail(),
						projection.getPhoneNumber(),
						projection.getFirstName(),
						projection.getMiddleName(),
						projection.getLastName(),
						projection.getProfilePicture()
				));
	}

	public Flux<WalletSummary> fetchWallets(String userId)
	{
		ObjectId id = MongoUtils.idToEntity(userId);
		if (id == null)
		{
			return Flux.empty();
		}
		return userMongoRepository
				.fetchWallets(id)
				.map(WalletSummaryProjection::toDomain);
	}
}
