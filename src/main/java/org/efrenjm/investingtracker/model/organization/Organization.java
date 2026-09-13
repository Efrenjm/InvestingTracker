package org.efrenjm.investingtracker.model.organization;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.organization.account.Account;
import org.efrenjm.investingtracker.model.organization.role.UserRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document(collection = "organizations")
public class Organization {
    @Id
    @Field("_id")
    private ObjectId id;

    private String name;

    private String description;

    private List<UserRole> members;

    @Field("created_by")
    private ObjectId createdBy;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    private List<Account> accounts;

    private OrganizationConfig configuration;
}
