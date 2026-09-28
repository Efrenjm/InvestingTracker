package org.efrenjm.investingtracker.domain.model.account;

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
public class AccountConfig {
    private String color;
    private String icon;
    private Boolean visible;
    private String image;
    private Boolean includedInNetSum;
    private String group;

    public static AccountConfig defaultConfig() {
        return AccountConfig.builder()
                .color("#000000")
                .icon("account_balance")
                .visible(true)
                .image(null)
                .includedInNetSum(true)
                .group("default")
                .build();
    }
}
