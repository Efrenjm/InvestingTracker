package org.efrenjm.investingtracker.model.Organization;

import lombok.*;
import org.efrenjm.investingtracker.model.Account.Account;
import org.efrenjm.investingtracker.model.Profile.PublicProfile;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "organizations")
public class Organization {
    private String id;
    private String name;
    private String description;
    private List<UserRole> users;
    private PublicProfile createdBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<Account> accounts;
}
