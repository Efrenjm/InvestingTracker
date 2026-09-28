package org.efrenjm.investingtracker.domain.service;

import java.util.HashMap;
import java.util.Map;
import org.efrenjm.investingtracker.domain.model.wallet.TransactionSuperCategory;
import org.springframework.stereotype.Service;

@Service
public class TransactionCategoryDomainService {
    public Map<String, TransactionSuperCategory> createDefaultTransactionCategories() {
        Map<String, TransactionSuperCategory> categories = HashMap.newHashMap(3);
        categories.put("Transference", createTransferenceCategories());

        return categories;
    }

    private TransactionSuperCategory createTransferenceCategories() {
        return TransactionSuperCategory.builder()
                .description("Movements between accounts")
                .color("#F44336") // Red
                .icon("mdi:cash-minus")
                .build();
    }
}
