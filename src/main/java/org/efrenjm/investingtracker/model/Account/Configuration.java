package org.efrenjm.investingtracker.model.Account;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Configuration {
    private String color;
    private String icon;
    private Boolean visible;
    private String image;
    private Boolean includedInNetSum;
    private String group;
}
