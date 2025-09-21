package org.efrenjm.investingtracker.domain.model.wallet;

import lombok.*;
import org.efrenjm.investingtracker.domain.model.transaction.Rule;

import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class WalletConfig
{
	private Set<String> rules;
	private Map<String, TransactionSuperCategory> transactionCategories;
}
