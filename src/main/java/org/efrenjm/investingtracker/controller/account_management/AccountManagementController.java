package org.efrenjm.investingtracker.controller.account_management;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.config.AuthenticatedUser;
import org.efrenjm.investingtracker.dto.controller.account_management.CreateAccountRequestDTO;
import org.efrenjm.investingtracker.dto.model.account.AccountSummary;
import org.efrenjm.investingtracker.model.user.User;
import org.efrenjm.investingtracker.service.account_management.AccountManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/account")
public class AccountManagementController {
	private final AccountManagementService accountManagementService;

	@GetMapping
	public Mono<ResponseEntity<List<AccountSummary>>> getAllAccounts(@RequestParam(required = false) ObjectId organizationId,
	                                                                 @AuthenticatedUser User user) {
		if (organizationId == null) {
			return accountManagementService.getAllUserAccounts(user)
					.map(ResponseEntity::ok);
		} else if (user.getOrganizations().contains(organizationId)) {
			return accountManagementService.getAllOrganizationAccounts(organizationId)
					.map(ResponseEntity::ok);
		} else {
			return Mono.error(new IllegalArgumentException("Organization not found"));
		}
	}

	@GetMapping("/{accountId}")
	public Mono<ResponseEntity<Void>> getAccount(@PathVariable ObjectId accountId) {
		return null;
	}

	@PostMapping
	public Mono<ResponseEntity<Void>> createAccount(@RequestBody CreateAccountRequestDTO accountToCreate) {
		return null;
	}

	@PutMapping("/{accountId}")
	public Mono<ResponseEntity<Void>> updateAccount(@PathVariable ObjectId accountId) {
		return null;
	}

	@DeleteMapping("/{accountId}")
	public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable ObjectId accountId) {
		return null;
	}
}
