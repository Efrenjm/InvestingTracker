package org.efrenjm.investingtracker.model.organization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.account.Account;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
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

    private List<UserRole> users;

    @DBRef
    @JsonBackReference
    @Field("created_by")
    private Profile createdBy;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    private List<Account> accounts;
}
