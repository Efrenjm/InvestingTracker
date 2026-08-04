package org.efrenjm.investingtracker.domain.service;

import org.efrenjm.investingtracker.domain.model.wallet.TransactionSuperCategory;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionCategoryDomainServiceTest {

	private final TransactionCategoryDomainService transactionCategoryDomainService = new TransactionCategoryDomainService();

	@Test
	void createDefaultTransactionCategories_ShouldIncludeTransferenceWithExpectedMetadata() {
		Map<String, TransactionSuperCategory> categories = transactionCategoryDomainService.createDefaultTransactionCategories();

		assertTrue(categories.containsKey("Transference"));
		TransactionSuperCategory transference = categories.get("Transference");
		assertEquals("Movements between accounts", transference.getDescription());
		assertEquals("#F44336", transference.getColor());
		assertEquals("mdi:cash-minus", transference.getIcon());
	}
}
