package org.efrenjm.investingtracker.domain.service;

import org.efrenjm.investingtracker.domain.model.wallet.Role;
import org.efrenjm.investingtracker.domain.model.wallet.Visibility;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.efrenjm.investingtracker.domain.ports.outbound.utils.IdGeneratorPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletDomainServiceTest {

	@Mock
	private IdGeneratorPort idGenerator;

	@Mock
	private RoleDomainService roleDomainService;

	@Mock
	private TransactionCategoryDomainService transactionCategoryDomainService;

	@InjectMocks
	private WalletDomainService walletDomainService;

	private Map<String, Role> defaultRoles;

	@BeforeEach
	void setUp() {
		defaultRoles = new HashMap<>();
		Role ownerRole = Role.builder().description("Owner").members(new HashSet<>()).build();
		Role managerRole = Role.builder().description("Manager").members(new HashSet<>()).build();
		defaultRoles.put("Owner", ownerRole);
		defaultRoles.put("Manager", managerRole);
	}

	@Test
	void createWallet_WithVisibility_CreatesWalletWithOwner() {
		String userId = "user-123";
		String walletId = "wallet-456";
		String name = "My Investments";
		String description = "Main portfolio";

		when(idGenerator.generateId()).thenReturn(walletId);
		when(roleDomainService.createDefaultRoles()).thenReturn(defaultRoles);
		when(transactionCategoryDomainService.createDefaultTransactionCategories()).thenReturn(new HashMap<>());

		Wallet wallet = walletDomainService.createWallet(userId, name, description, Visibility.PUBLIC);

		assertNotNull(wallet);
		assertEquals(walletId, wallet.getId());
		assertEquals(name, wallet.getName());
		assertEquals(description, wallet.getDescription());
		assertEquals(Visibility.PUBLIC, wallet.getVisibility());
		assertEquals(userId, wallet.getCreatedBy());
		assertTrue(wallet.getRoles().isPresent());
		assertTrue(wallet.getRoles().get().get("Owner").getMembers().contains(userId));
	}

	@Test
	void createWallet_WithoutVisibility_DefaultsToPrivate() {
		String userId = "user-123";
		String walletId = "wallet-789";

		when(idGenerator.generateId()).thenReturn(walletId);
		when(roleDomainService.createDefaultRoles()).thenReturn(defaultRoles);
		when(transactionCategoryDomainService.createDefaultTransactionCategories()).thenReturn(new HashMap<>());

		Wallet wallet = walletDomainService.createWallet(userId, "Private Wallet", "Secret");

		assertEquals(Visibility.PRIVATE, wallet.getVisibility());
		assertTrue(wallet.getRoles().get().get("Owner").getMembers().contains(userId));
	}
}
