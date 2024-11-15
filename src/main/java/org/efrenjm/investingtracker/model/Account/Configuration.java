package org.efrenjm.investingtracker.model.Account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {
    private String color;
    private String icon;
    private Boolean visible;
    private String image;
    private Boolean includedInNetSum;
    private String group;
}
