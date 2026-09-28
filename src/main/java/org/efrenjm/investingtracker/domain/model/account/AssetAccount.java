package org.efrenjm.investingtracker.domain.model.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class AssetAccount extends BaseAccount {
    private String asset;

    private Double currentPrice;

    private Double averageCost;

    private Double goal;
}
