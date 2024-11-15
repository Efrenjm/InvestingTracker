package org.efrenjm.investingtracker.model.Rule;

import lombok.*;
import org.efrenjm.investingtracker.model.Organization.Organization;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "rules")
public class Rule {
    @Id private String id;
    private String name;
    private String description;
    private Organization organization;
    private Options options;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<String> tags;
}
