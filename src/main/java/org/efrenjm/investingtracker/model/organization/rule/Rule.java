package org.efrenjm.investingtracker.model.organization.rule;

import lombok.*;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Rule {
    @Id
    @Field("_id")
    private String id;

    private String name;

    private String description;

    private RuleConfig ruleConfig;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    private List<String> tags;
}
