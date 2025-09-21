package org.efrenjm.investingtracker.domain.ports.outbound.repository;

import org.efrenjm.investingtracker.domain.dto.AccountSummary;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.infrastructure.security.SecurityUserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
	Mono<User> findById(String userId);

//	Mono<Profile> findProfile(String email);

	Mono<SecurityUserDetails> findSecurityUser(String username);

	Mono<User> findByAnyCredential(String credential);

	Mono<User> save(User user);

	Mono<Void> delete(String userId);

	Mono<User> findEmailInUse(String email);

	Mono<User> findPhoneInUse(String phone);

	Flux<AccountSummary> fetchAccounts(String userId);

	Flux<PublicProfile> fetchFriends(String userId);

	Flux<WalletSummary> fetchWallets(String userId);
}
