package org.efrenjm.investingtracker.domain.model.wallet;

import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class WalletConfig {
    private Set<String> rules;
    private Map<String, TransactionSuperCategory> transactionCategories;
}
