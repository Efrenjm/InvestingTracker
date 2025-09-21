package org.efrenjm.investingtracker.domain.model.account;

import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class AssetAccount extends BaseAccount
{
	private String asset;

	private Double currentPrice;

	private Double averageCost;

	private Double goal;
}
