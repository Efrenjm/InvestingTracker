package org.efrenjm.investingtracker.domain.service;

import org.efrenjm.investingtracker.domain.model.wallet.Role;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RoleDomainServiceTest {

	private final RoleDomainService roleDomainService = new RoleDomainService();

	@Test
	void createDefaultRoles_ShouldReturnOwnerManagerViewerWithExpectedPermissions() {
		Map<String, Role> roles = roleDomainService.createDefaultRoles();

		assertTrue(roles.containsKey("Owner"));
		assertTrue(roles.containsKey("Manager"));
		assertTrue(roles.containsKey("Viewer"));

		Role owner = roles.get("Owner");
		assertTrue(owner.getPermissions().getAccounts().isAdd());
		assertTrue(owner.getPermissions().getAccounts().isEdit());
		assertTrue(owner.getPermissions().getAccounts().isView());
		assertTrue(owner.getPermissions().getAccounts().isRemove());
		assertTrue(owner.getPermissions().getWallet().isEdit());
		assertTrue(owner.getPermissions().getWallet().isRemove());

		Role manager = roles.get("Manager");
		assertTrue(manager.getPermissions().getAccounts().isAdd());
		assertTrue(manager.getPermissions().getAccounts().isEdit());
		assertTrue(manager.getPermissions().getAccounts().isView());
		assertFalse(manager.getPermissions().getAccounts().isRemove());
		assertTrue(manager.getPermissions().getWallet().isView());
		assertFalse(manager.getPermissions().getWallet().isEdit());
		assertFalse(manager.getPermissions().getWallet().isRemove());

		Role viewer = roles.get("Viewer");
		assertFalse(viewer.getPermissions().getAccounts().isAdd());
		assertFalse(viewer.getPermissions().getAccounts().isEdit());
		assertTrue(viewer.getPermissions().getAccounts().isView());
		assertFalse(viewer.getPermissions().getAccounts().isRemove());
		assertTrue(viewer.getPermissions().getWallet().isView());
		assertFalse(viewer.getPermissions().getWallet().isEdit());
		assertFalse(viewer.getPermissions().getWallet().isRemove());
	}
}
