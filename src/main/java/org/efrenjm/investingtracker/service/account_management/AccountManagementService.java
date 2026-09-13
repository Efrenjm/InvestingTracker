package org.efrenjm.investingtracker.service.account_management;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.dto.model.account.AccountSummary;
import org.efrenjm.investingtracker.model.organization.account.Account;
import org.efrenjm.investingtracker.model.user.User;
import org.efrenjm.investingtracker.service.model.organization.OrganizationService;
import org.efrenjm.investingtracker.service.model.user.UserService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class AccountManagementService {
	private final UserService userService;
	private final OrganizationService organizationService;
//	public Mono<Account> getAccountDetails() {
//
//	}

	public Mono<List<AccountSummary>> getAllUserAccounts(User user) {
		return userService.fetchAccounts(user.getId()).collectList()
				.switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")));
	}

	public Mono<List<AccountSummary>> getAllOrganizationAccounts(ObjectId organizationId) {
		return organizationService.fetchAccounts(organizationId).collectList()
				.switchIfEmpty(Mono.error(new IllegalArgumentException("Organization not found")));
	}
}
