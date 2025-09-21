package org.efrenjm.investingtracker.domain.model.account;

import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class CreditAccount extends BaseAccount
{
	private Double currentDebt;

	private Double creditLimit;
}
