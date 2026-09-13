package org.efrenjm.investingtracker.model.Account;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

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

    @Field("included_in_net_sum")
    private Boolean includedInNetSum;

    private String group;
}
