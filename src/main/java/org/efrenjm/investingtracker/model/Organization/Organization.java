package org.efrenjm.investingtracker.model.Organization;

import lombok.*;
import org.efrenjm.investingtracker.model.Account.Account;
import org.efrenjm.investingtracker.model.Profile.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "organizations")
public class Organization {
    @Id
    @Field("_id")
    private String id;

    private String name;

    private String description;

    private List<UserRole> users;

    @Field("created_by")
    private Profile createdBy;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;

    private List<Account> accounts;
}
