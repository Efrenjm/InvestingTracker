package org.efrenjm.investingtracker.model.Profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.efrenjm.investingtracker.model.Organization.Organization;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "profiles")
public class Profile {

    @Id
    private String id;
    private String username;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePicture;
    private List<Organization> organizations;
    private Boolean active;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime lastLogin;
    private List<PublicProfile> friends;
}
