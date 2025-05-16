package org.efrenjm.investingtracker.domain.model.wallet;

import lombok.*;
import org.efrenjm.investingtracker.domain.model.transaction.Rule;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class WalletConfig {
	private List<Rule> rules;
	private List<TransactionCategory> transactionCategories;

	public static WalletConfig defaultConfig() {
		return new WalletConfig(
				List.of(),
				List.of()
		);
	}
}
