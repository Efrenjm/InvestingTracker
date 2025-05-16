package org.efrenjm.investingtracker.domain.model.transaction;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AccountAllocation {
	private String account;
	private double amount;
}
